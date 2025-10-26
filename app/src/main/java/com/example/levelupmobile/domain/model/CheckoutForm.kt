package com.example.levelupmobile.domain.model

data class CheckoutForm(
    val name: String,
    val phone: String,
    val address: String,
    val deliveryMethod: String,
    val paymentMethod: String
)

