package com.example.levelupmobile.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.vm.models.ProductUi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repo: ShopRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int? =
        savedStateHandle.get<String>("productId")?.toIntOrNull()

    val ui: StateFlow<ProductUi?> =
        repo.observeProducts()
            .map { list ->
                val p = list.firstOrNull { it.id == productId }
                p?.let { ProductUi(it.id, it.name, it.description, it.priceNeto, it.imageUrl) }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun addToCart() {
        val id = productId ?: return
        viewModelScope.launch { repo.addToCart(id) }
    }
}
