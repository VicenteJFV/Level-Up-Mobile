package com.example.levelupmobile.vm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.vm.CartViewModel

class CartVMFactory(
    private val repo: ShopRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CartViewModel(repo) as T
    }
}
