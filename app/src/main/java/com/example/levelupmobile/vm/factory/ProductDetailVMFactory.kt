package com.example.levelupmobile.vm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.vm.ProductDetailViewModel

class ProductDetailVMFactory(
    private val repo: ShopRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()
        @Suppress("UNCHECKED_CAST")
        return ProductDetailViewModel(repo, handle) as T
    }
}
