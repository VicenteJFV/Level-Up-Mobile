package com.example.levelupmobile.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.nav.Routes
import com.example.levelupmobile.vm.models.ProductUi
import com.example.levelupmobile.vm.models.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repo: ShopRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _ui = MutableStateFlow<ProductUi?>(null)
    val ui: StateFlow<ProductUi?> = _ui

    private val pid: Int? = savedStateHandle.get<String>(Routes.ProductDetail.ARG)?.toIntOrNull()

    init {
        viewModelScope.launch {
            repo.observeProducts()
                .map { list ->
                    val p = pid?.let { idInt -> list.firstOrNull { it.id == idInt } }
                    p?.toUi()
                }
                .collect { _ui.value = it }
        }
    }

    fun addToCart() {
        val id = pid ?: return
        viewModelScope.launch { repo.addToCart(id) }
    }
}
