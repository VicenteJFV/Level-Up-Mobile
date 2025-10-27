package com.example.levelupmobile.data.local

import com.example.levelupmobile.data.dao.CartDao
import com.example.levelupmobile.data.dao.ProductDao
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
    //Observa productos por ID
    override fun observeById(id: String): Flow<Product?> =
        productDao.observeById(id).map { it?.toDomain() }

    //Observa productos
    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeAll().map { list -> list.map { it.toDomain() } }

    //Observa carrito
    override fun observeCart(): Flow<List<CartLine>> =
        cartDao.observeCart().map { list -> list.map { it.toDomain() } }

    //Añadir al carrito
    override suspend fun addToCart(productId: String, qty: Int) {
        val existing = cartDao.findByProduct(productId)
        if (existing == null) {
            cartDao.insert(CartLineEntity(productId = productId, qty = qty))
        } else {
            cartDao.update(existing.copy(qty = existing.qty + qty))
        }
    }

    //Modificar cantidad directamente
    override suspend fun setQty(productId: String, qty: Int) {
        val existing = cartDao.findByProduct(productId) ?: return
        if (qty <= 0) cartDao.deleteByProduct(productId)
        else cartDao.update(existing.copy(qty = qty))
    }

    //Eliminar producto del carrito
    override suspend fun removeFromCart(productId: String) {
        cartDao.deleteByProduct(productId)
    }

    //Vaciar carrito
    override suspend fun clearCart() {
        cartDao.clear()
    }

    //Checkout: calcula totales e IVA
    override suspend fun checkout(form: CheckoutForm): OrderSummary {
        val cartLines = cartDao.observeCart().first()
        val products = productDao.observeAll().first()
        val catalog = products.associateBy { it.id }

        var totalNeto = 0L
        cartLines.forEach { line ->
            val product = catalog[line.productId]
            if (product != null) {
                totalNeto += product.priceNeto * line.qty
            }
        }

        val iva = (totalNeto * 0.19).toLong()
        val total = totalNeto + iva

        // Vacía el carrito tras checkout
        cartDao.clear()

        return OrderSummary(
            orderId = System.currentTimeMillis(),
            totalNeto = totalNeto,
            iva = iva,
            total = total,
            createdAt = System.currentTimeMillis()
        )
    }
}
