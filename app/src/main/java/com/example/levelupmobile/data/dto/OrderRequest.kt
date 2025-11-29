package com.example.levelupmobile.data.dto

data class OrderRequest(
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val paymentMethod: String,
    val totalAmount: Double,
    val items: List<OrderItemRequest>
)