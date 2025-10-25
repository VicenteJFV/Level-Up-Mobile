package com.example.levelupmobile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.vm.models.CartItemUi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
        val catalog = products.associateBy { it.id } // clave String
        val items = lines.mapNotNull { line ->
            val p = catalog[line.productId] ?: return@mapNotNull null
            CartItemUi(p.id, p.name, p.priceNeto, line.qty)
        }
        val subtotal = items.sumOf { it.lineTotal }
        val iva = (subtotal * 0.19).toLong()
        CartUiState(items, subtotal, iva, subtotal + iva)
    }.stateIn(viewModelScope, SharingStarted.Lazily, CartUiState())

    fun addItem(productId: String, qty: Int = 1) = viewModelScope.launch {
        repo.addToCart(productId, qty)
    }
    fun setQty(productId: String, qty: Int) = viewModelScope.launch { repo.setQty(productId, qty) }
    fun removeItem(productId: String) = viewModelScope.launch { repo.removeFromCart(productId) }
    fun clear() = viewModelScope.launch { repo.clearCart() }
}
