package com.example.levelupmobile.vm.factory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.vm.ProductDetailViewModel

class ProductDetailVMFactory(
    private val repo: ShopRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ProductDetailViewModel::class.java)
        return ProductDetailViewModel(repo, savedStateHandle) as T
    }
}
