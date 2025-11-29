package com.example.levelupmobile.domain.repo

import com.example.levelupmobile.data.dto.OrderResponse
import com.example.levelupmobile.domain.model.CartLine
import com.example.levelupmobile.domain.model.CheckoutForm
import com.example.levelupmobile.domain.model.OrderSummary
import com.example.levelupmobile.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeShopRepository : ShopRepository {

    // Catálogo de ejemplo
    private val products = listOf(
        Product("CO001", "PlayStation 5", "Consola...", 549_990),
        Product("JM001", "Catan", "Juego de mesa...", 29_990),
        Product("MS001", "Mouse Logitech G502", "Gaming mouse", 49_990),
    )

    private val productsFlow = MutableStateFlow(products)

    // Carrito en memoria
    private val cart = MutableStateFlow<List<CartLine>>(emptyList())

    // ---------- Catálogo ----------
    override fun observeProducts(): Flow<List<Product>> = productsFlow

    override suspend fun getById(id: String): Product? =
        products.firstOrNull { it.id == id }

    override fun observeById(id: String): Flow<Product?> =
        productsFlow.map { list -> list.firstOrNull { it.id == id } }

    // ---------- Carrito ----------
    override fun observeCart(): Flow<List<CartLine>> = cart

    override suspend fun addToCart(productId: String, qty: Int) {
        val current = cart.value.toMutableList()
        val idx = current.indexOfFirst { it.productId == productId }
        if (idx >= 0) {
            val line = current[idx]
            current[idx] = line.copy(qty = line.qty + qty)
        } else {
            current += CartLine(productId, qty)
        }
        cart.value = current
    }

    override suspend fun setQty(productId: String, qty: Int) {
        cart.value = cart.value
            .map { if (it.productId == productId) it.copy(qty = qty) else it }
            .filter { it.qty > 0 }
    }

    override suspend fun removeFromCart(productId: String) {
        cart.value = cart.value.filterNot { it.productId == productId }
    }

    override suspend fun clearCart() {
        cart.value = emptyList()
    }

    // ---------- Checkout ----------
    override suspend fun checkout(form: CheckoutForm): OrderSummary {
        val lines = cart.value
        val catalog = products.associateBy { it.id }

        var totalNeto = 0L
        lines.forEach { line ->
            val product = catalog[line.productId]
            if (product != null) {
                totalNeto += product.priceNeto * line.qty
            }
        }
        val iva = (totalNeto * 0.19).toLong()
        val total = totalNeto + iva

        // vacía el carrito tras comprar
        cart.value = emptyList()

        return OrderSummary(
            orderId = System.currentTimeMillis(),
            totalNeto = totalNeto,
            iva = iva,
            total = total,
            createdAt = System.currentTimeMillis()
        )
    }

    // ---------- Gestión de Órdenes (FAKE/DUMMY) ----------
    // Estos métodos no hacen nada real, solo devuelven null/false
    // porque FakeShopRepository es solo para testing local sin API

    override suspend fun getOrder(orderId: Long): OrderResponse? {
        // No implementado en el repositorio fake
        return null
    }

    override suspend fun updateOrder(orderId: Long, phone: String, address: String): OrderResponse? {
        // No implementado en el repositorio fake
        return null
    }

    override suspend fun confirmOrder(orderId: Long): OrderResponse? {
        // No implementado en el repositorio fake
        return null
    }

    override suspend fun cancelOrder(orderId: Long): Boolean {
        // No implementado en el repositorio fake
        return false
    }
}