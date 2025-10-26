package com.example.levelupmobile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.domain.repo.ShopRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// --- Definición LOCAL para evitar problemas de paquetes/imports ---
data class CartItemUi(
    val id: String,
    val name: String,
    val price: Long,     // precio unitario
    val qty: Int
) {
    val lineTotal: Long get() = price * qty
}

data class CartUiState(
    val items: List<CartItemUi> = emptyList(),
    val subtotal: Long = 0L,
    val iva: Long = 0L,
    val total: Long = 0L
)

class CartViewModel(private val repo: ShopRepository) : ViewModel() {

    val ui: StateFlow<CartUiState> = combine(
        repo.observeCart(),
        repo.observeProducts()
    ) { lines, products ->
        val catalog = products.associateBy { it.id }

        val items: List<CartItemUi> = lines.mapNotNull { line ->
            val p = catalog[line.productId] ?: return@mapNotNull null
            CartItemUi(
                id = p.id,
                name = p.name,
                price = p.priceNeto, // del dominio
                qty = line.qty
            )
        }

        val subtotal = items.sumOf { it.lineTotal }
        val iva = (subtotal * 0.19).toLong()
        val total = subtotal + iva

        CartUiState(
            items = items,
            subtotal = subtotal,
            iva = iva,
            total = total
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, CartUiState())

    // Acciones
    fun addItem(productId: String, qty: Int = 1) = viewModelScope.launch {
        repo.addToCart(productId, qty)
    }

    fun setQty(productId: String, qty: Int) = viewModelScope.launch {
        repo.setQty(productId, qty)
    }

    fun removeItem(productId: String) = viewModelScope.launch {
        repo.removeFromCart(productId)
    }

    fun clear() = viewModelScope.launch { repo.clearCart() }

    // Helpers +/-
    fun inc(productId: String) = viewModelScope.launch { repo.addToCart(productId, 1) }

    fun dec(productId: String) = viewModelScope.launch {
        val current = ui.value.items.firstOrNull { it.id == productId }?.qty ?: 0
        val next = (current - 1).coerceAtLeast(0)
        if (next == 0) repo.removeFromCart(productId) else repo.setQty(productId, next)
    }
}
