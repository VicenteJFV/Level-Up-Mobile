package com.example.levelupmobile.domain.repo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeShopRepository : ShopRepository {

    private val products = listOf(
        Product(1, "Headset Gamer", "7.1", 29990),
        Product(2, "Control Pro", "Ergonómico", 19990),
        Product(3, "Mouse RGB", "16000 DPI", 14990)
    )
    private val cart = MutableStateFlow<List<CartLine>>(emptyList())

    override fun observeProducts() = MutableStateFlow(products).asStateFlow()
    override fun observeCart() = cart.asStateFlow()

    override suspend fun addToCart(productId: Int, qty: Int) {
        val current = cart.value.toMutableList()
        val idx = current.indexOfFirst { it.productId == productId }
        if (idx >= 0) current[idx] = current[idx].copy(qty = current[idx].qty + qty)
        else current += CartLine(productId, qty)
        cart.value = current
    }

    override suspend fun setQty(productId: Int, qty: Int) {
        cart.value = cart.value.map { if (it.productId == productId) it.copy(qty = qty) else it }
            .filter { it.qty > 0 }
    }

    override suspend fun removeFromCart(productId: Int) {
        cart.value = cart.value.filterNot { it.productId == productId }
    }

    override suspend fun clearCart() { cart.value = emptyList() }

    override suspend fun checkout(form: CheckoutForm): OrderSummary {
        // Simular cálculo
        val catalog = products.associateBy { it.id }
        val subtotal = cart.value.sumOf { (catalog[it.productId]?.priceNeto ?: 0L) * it.qty }
        val iva = (subtotal * 0.19).toLong()
        val total = subtotal + iva
        delay(300) // simulate IO
        cart.value = emptyList()
        return OrderSummary(orderId = System.currentTimeMillis(), totalNeto = subtotal, iva = iva, total = total)
    }
}
