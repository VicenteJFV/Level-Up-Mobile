package com.example.levelupmobile.data.dto

data class OrderItemRequest(
    val productId: String,
    val quantity: Int,
    val productName: String,
    val unitPrice: Double
)