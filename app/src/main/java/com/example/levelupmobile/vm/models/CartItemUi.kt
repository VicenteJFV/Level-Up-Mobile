package com.example.levelupmobile.vm.models

data class CartItemUi(
    val productId: String,
    val name: String,
    val price: Long,
    val qty: Int
) {
    val lineTotal: Long get() = price * qty
}
