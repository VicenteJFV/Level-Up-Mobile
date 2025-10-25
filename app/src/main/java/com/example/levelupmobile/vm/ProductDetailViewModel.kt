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

    private val pid: String? = savedStateHandle.get<String>(Routes.ProductDetail.ARG)

    private val _ui = MutableStateFlow<ProductUi?>(null)
    val ui: StateFlow<ProductUi?> = _ui

    init {
        viewModelScope.launch {
            repo.observeProducts()
                .map { list -> list.firstOrNull { it.id == pid }?.toUi() }
                .collect { _ui.value = it }
        }
    }

    fun addToCart() {
        pid ?: return
        viewModelScope.launch { repo.addToCart(pid) }
    }
}

