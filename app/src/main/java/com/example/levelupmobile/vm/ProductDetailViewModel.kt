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
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repo: ShopRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val code: String = savedStateHandle.get<String>(Routes.ProductDetail.ARG) ?: ""

    private val _ui = MutableStateFlow<ProductUi?>(null)
    val ui: StateFlow<ProductUi?> = _ui

    init {
        load()
    }

    private fun load() = viewModelScope.launch {
        val p = repo.getById(code)
        _ui.value = p?.toUi()
    }

    fun addToCart() = viewModelScope.launch {
        if (code.isNotBlank()) repo.addToCart(code, 1)
    }
}
