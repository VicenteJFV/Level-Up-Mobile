package com.example.levelupmobile.data.local

import com.example.levelupmobile.data.api.OrderRetrofitClient
import com.example.levelupmobile.data.dao.CartDao
import com.example.levelupmobile.data.dao.ProductDao
import com.example.levelupmobile.data.dto.OrderItemRequest
import com.example.levelupmobile.data.dto.OrderRequest
import com.example.levelupmobile.data.entity.CartLineEntity
import com.example.levelupmobile.domain.model.*
import com.example.levelupmobile.domain.repo.ShopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ShopRepositoryImpl(
    private val productDao: ProductDao,
    private val cartDao: CartDao
) : ShopRepository {

    override suspend fun getById(id: String): Product? =
        productDao.getById(id)?.toDomain()

    override fun observeById(id: String): Flow<Product?> =
        productDao.observeById(id).map { it?.toDomain() }

    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeCart(): Flow<List<CartLine>> =
        cartDao.observeCart().map { list -> list.map { it.toDomain() } }

    override suspend fun addToCart(productId: String, qty: Int) {
        val existing = cartDao.findByProduct(productId)
        if (existing == null) {
            cartDao.insert(CartLineEntity(productId = productId, qty = qty))
        } else {
            cartDao.update(existing.copy(qty = existing.qty + qty))
        }
    }

    override suspend fun setQty(productId: String, qty: Int) {
        val existing = cartDao.findByProduct(productId) ?: return
        if (qty <= 0) cartDao.deleteByProduct(productId)
        else cartDao.update(existing.copy(qty = qty))
    }

    override suspend fun removeFromCart(productId: String) {
        cartDao.deleteByProduct(productId)
    }

    override suspend fun clearCart() {
        cartDao.clear()
    }

    override suspend fun checkout(form: CheckoutForm): OrderSummary {
        // 1. Obtener carrito y productos
        val cartLines = cartDao.observeCart().first()
        val products = productDao.observeAll().first()
        val catalog = products.associateBy { it.id }

        // 2. Calcular totales
        var totalNeto = 0L
        val items = mutableListOf<OrderItemRequest>()

        cartLines.forEach { line ->
            val product = catalog[line.productId]
            if (product != null) {
                val lineTotal = product.priceNeto * line.qty
                totalNeto += lineTotal

                // Crear item para la API (orden corregido)
                items.add(
                    OrderItemRequest(
                        productId = product.id,
                        quantity = line.qty,
                        productName = product.name,
                        unitPrice = product.priceNeto.toDouble()
                    )
                )
            }
        }

        val iva = (totalNeto * 0.19).toLong()
        val total = totalNeto + iva

        // 3. Crear request para la API
        val orderRequest = OrderRequest(
            customerName = form.name,
            customerPhone = form.phone,
            deliveryAddress = form.address,
            paymentMethod = form.paymentMethod,
            totalAmount = total.toDouble(),
            items = items
        )

        // 4. Llamar a la API
        val response = OrderRetrofitClient.api.createOrder(orderRequest)

        if (response.isSuccessful && response.body() != null) {
            val orderResponse = response.body()!!

            // 5. Limpiar carrito tras éxito
            cartDao.clear()

            // 6. Retornar OrderSummary con el ID real del backend
            return OrderSummary(
                orderId = orderResponse.id,
                totalNeto = totalNeto,
                iva = iva,
                total = total,
                createdAt = System.currentTimeMillis()
            )
        } else {
            throw Exception("Error al crear la orden: ${response.code()} - ${response.message()}")
        }
    }
}