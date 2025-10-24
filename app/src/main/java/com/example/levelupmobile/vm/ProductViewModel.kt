package com.example.levelupmobile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.domain.repo.Product
import com.example.levelupmobile.domain.repo.ShopRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProductViewModel(repo: ShopRepository) : ViewModel() {
    val products: StateFlow<List<Product>> =
        repo.observeProducts().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
