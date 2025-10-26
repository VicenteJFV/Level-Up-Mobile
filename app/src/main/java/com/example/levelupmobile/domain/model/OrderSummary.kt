package com.example.levelupmobile.domain.model

data class OrderSummary(
    val orderId: Long,
    val totalNeto: Long,
    val iva: Long,
    val total: Long,
    val createdAt: Long = System.currentTimeMillis()
)
