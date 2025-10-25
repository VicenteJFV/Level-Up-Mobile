package com.example.levelupmobile.vm.models

data class ProductUi(
    val id: String,
    val name: String,
    val description: String,
    val price: Long,
    val imageUrl: String? = null
)
