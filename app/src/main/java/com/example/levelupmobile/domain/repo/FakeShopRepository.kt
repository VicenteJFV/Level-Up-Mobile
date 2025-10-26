package com.example.levelupmobile.domain.repo

import com.example.levelupmobile.domain.model.CartLine
import com.example.levelupmobile.domain.model.CheckoutForm
import com.example.levelupmobile.domain.model.OrderSummary
import com.example.levelupmobile.domain.model.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeShopRepository : ShopRepository {

    private val products = listOf(
        Product("CO001", "PlayStation 5", "Consola...", 549_990),
        Product("JM001", "Catan", "Juego de mesa...", 29_990),
        Product("MS001", "Mouse Logitech G502", "Gaming mouse", 49_990),
    )

    private val productsFlow = MutableStateFlow(products)
    private val cart = MutableStateFlow<List<CartLine>>(emptyList())

    override fun observeProducts(): Flow<List<Product>> = productsFlow
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
        cart.value = cart.value.map {
            if (it.productId == productId) it.copy(qty = qty) else it
        }.filter { it.qty > 0 }
    }

    override suspend fun removeFromCart(productId: String) {
        cart.value = cart.value.filterNot { it.productId == productId }
    }

    override suspend fun clearCart() {
        cart.value = emptyList()
    }

    override suspend fun checkout(form: CheckoutForm): OrderSummary {
        // 1 Tomamos el carrito actual
        val lines = cart.value
        val catalog = products.associateBy { it.id }

        // 2 Calculamos el total neto (sin IVA)
        var totalNeto = 0L
        lines.forEach { line ->
            val product = catalog[line.productId]
            if (product != null) {
                totalNeto += product.priceNeto * line.qty
            }
        }

        // 3 Calculamos IVA (19%) y total final
        val iva = (totalNeto * 0.19).toLong()
        val total = totalNeto + iva

        // 4️ Limpiamos carrito (simulando compra finalizada)
        cart.value = emptyList()

        // 5️ Retornamos resumen de la orden
        return OrderSummary(
            orderId = System.currentTimeMillis(), // único por timestamp
            totalNeto = totalNeto,
            iva = iva,
            total = total,
            createdAt = System.currentTimeMillis()
        )
    }

}

