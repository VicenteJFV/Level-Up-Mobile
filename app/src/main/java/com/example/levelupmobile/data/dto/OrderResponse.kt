package com.example.levelupmobile.data.dto

data class OrderResponse(
    val id: Long,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val paymentMethod: String,
    val status: String,
    val createdAt: String,
    val totalAmount: Double,
    val items: List<OrderItemResponse>
)

data class OrderItemResponse(
    val productId: String,
    val quantity: Int,
    val productName: String,
    val unitPrice: Double
)