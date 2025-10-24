package com.example.levelupmobile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.domain.repo.CartLine
import com.example.levelupmobile.domain.repo.ShopRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartUi(
    val lines: List<CartLine> = emptyList(),
    val subtotal: Long = 0,
    val iva: Long = 0,
    val total: Long = 0
)

class CartViewModel(private val repo: ShopRepository) : ViewModel() {
    val ui: StateFlow<CartUi> = repo.observeCart().map { lines ->
        val catalog = repo.observeProducts().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()).value
            .associateBy { it.id }
        val subtotal = lines.sumOf { (catalog[it.productId]?.priceNeto ?: 0L) * it.qty }
        val iva = (subtotal * 0.19).toLong()
        CartUi(lines, subtotal, iva, subtotal + iva)
    }.stateIn(viewModelScope, SharingStarted.Lazily, CartUi())

    fun add(productId: Int) = viewModelScope.launch { repo.addToCart(productId) }
    fun setQty(productId: Int, qty: Int) = viewModelScope.launch { repo.setQty(productId, qty) }
    fun remove(productId: Int) = viewModelScope.launch { repo.removeFromCart(productId) }
}
