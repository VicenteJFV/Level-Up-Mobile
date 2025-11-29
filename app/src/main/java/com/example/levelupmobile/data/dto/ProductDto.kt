package com.example.levelupmobile.data.dto

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val stock: Int?,
    val platform: String?
)
