// kotlin
// File: `app/src/main/java/com/example/levelupmobile/data/local/ShopRepositoryImpl.kt`
package com.example.levelupmobile.data.local

import android.util.Log
import com.example.levelupmobile.data.api.CatalogRetrofitClient
import com.example.levelupmobile.data.api.OrderRetrofitClient
import com.example.levelupmobile.data.dao.CartDao
import com.example.levelupmobile.data.dao.ProductDao
import com.example.levelupmobile.data.dto.OrderItemRequest
import com.example.levelupmobile.data.dto.OrderRequest
import com.example.levelupmobile.data.dto.OrderResponse
import com.example.levelupmobile.data.entity.CartLineEntity
import com.example.levelupmobile.domain.model.*
import com.example.levelupmobile.domain.repo.ShopRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopRepositoryImpl(
    private val productDao: ProductDao,
    private val cartDao: CartDao
) : ShopRepository {

    private val _productsCache = MutableStateFlow<List<Product>>(emptyList())
    private val scope = CoroutineScope(Dispatchers.IO)

    // ========== CATÁLOGO (DESDE API) ==========

    override fun observeProducts(): Flow<List<Product>> {
        scope.launch {
            refreshProductsFromApi()
        }
        return _productsCache
    }

    private suspend fun refreshProductsFromApi() {
        try {
            val response = CatalogRetrofitClient.api.getAllProducts()
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!.map { it.toDomain() }
                _productsCache.value = products
                Log.d("ShopRepo", "Productos cargados desde API: ${products.size}")
            } else {
                Log.e("ShopRepo", "Error API: ${response.code()} - ${response.message()}")
                loadProductsFromRoom()
            }
        } catch (e: Exception) {
            Log.e("ShopRepo", "Error al cargar productos desde API: ${e.message}", e)
            loadProductsFromRoom()
        }
    }

    private suspend fun loadProductsFromRoom() {
        try {
            val localProducts = productDao.observeAll().first()
            _productsCache.value = localProducts.map { it.toDomain() }
            Log.d("ShopRepo", "Productos cargados desde Room (fallback): ${localProducts.size}")
        } catch (e: Exception) {
            Log.e("ShopRepo", "Error al cargar desde Room: ${e.message}", e)
        }
    }

    override suspend fun getById(id: String): Product? {
        return withContext(Dispatchers.IO) {
            try {
                val response = CatalogRetrofitClient.api.getProductById(id.toLong())
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.toDomain()
                } else {
                    productDao.getById(id)?.toDomain()
                }
            } catch (e: Exception) {
                Log.e("ShopRepo", "Error getById desde API: ${e.message}", e)
                productDao.getById(id)?.toDomain()
            }
        }
    }

    override fun observeById(id: String): Flow<Product?> {
        return _productsCache.map { products ->
            products.find { it.id == id }
        }
    }

    // ========== CARRITO ==========

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

    // ========== CHECKOUT ==========

    override suspend fun checkout(form: CheckoutForm): OrderSummary {
        val cartLines = cartDao.observeCart().first()
        val products = _productsCache.value
        val catalog = products.associateBy { it.id }

        var totalNeto = 0L
        val items = mutableListOf<OrderItemRequest>()

        cartLines.forEach { line ->
            val product = catalog[line.productId]
            if (product != null) {
                val lineTotal = product.priceNeto * line.qty
                totalNeto += lineTotal

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

        val orderRequest = OrderRequest(
            customerName = form.name,
            customerPhone = form.phone,
            deliveryAddress = form.address,
            paymentMethod = form.paymentMethod,
            totalAmount = total.toDouble(),
            items = items
        )

        val response = OrderRetrofitClient.api.createOrder(orderRequest)

        if (response.isSuccessful && response.body() != null) {
            val orderResponse = response.body()!!
            cartDao.clear()

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

    // ========== GESTIÓN DE ÓRDENES ==========

    override suspend fun getOrder(orderId: Long): OrderResponse? {
        return try {
            val response = OrderRetrofitClient.api.getOrder(orderId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateOrder(orderId: Long, phone: String, address: String): OrderResponse? {
        return try {
            val order = getOrder(orderId) ?: return null

            val updatedRequest = OrderRequest(
                customerName = order.customerName,
                customerPhone = phone,
                deliveryAddress = address,
                paymentMethod = order.paymentMethod,
                totalAmount = order.totalAmount,
                items = order.items.map {
                    OrderItemRequest(
                        productId = it.productId,
                        quantity = it.quantity,
                        productName = it.productName,
                        unitPrice = it.unitPrice
                    )
                }
            )

            val response = OrderRetrofitClient.api.updateOrder(orderId, updatedRequest)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun confirmOrder(orderId: Long): OrderResponse? {
        return try {
            val response = OrderRetrofitClient.api.confirmOrder(orderId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun cancelOrder(orderId: Long): Boolean {
        return try {
            val response = OrderRetrofitClient.api.deleteOrder(orderId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
