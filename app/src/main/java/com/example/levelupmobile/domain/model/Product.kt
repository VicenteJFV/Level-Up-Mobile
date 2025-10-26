package com.example.levelupmobile.domain.model

data class Product(
    val id: String,
    val name: String,
    val description: String = "",
    val priceNeto: Long,
    val ivaRate: Double = 0.19,
    val imageUrl: String? = null,
    val stock: Int = 999,
    val categoryId: Int? = null
)
