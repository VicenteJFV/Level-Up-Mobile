package com.example.levelupmobile.vm.models

data class ProductUi(
    val id: Int,
    val name: String,
    val description: String,
    val price: Long,
    val imageUrl: String? = null
)
