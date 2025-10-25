package com.example.levelupmobile.domain.repo

import kotlinx.coroutines.flow.Flow

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

data class CartLine(
    val productId: String,
    val qty: Int
)

data class OrderSummary(
    val orderId: Long,
    val totalNeto: Long,
    val iva: Long,
    val total: Long,
    val createdAt: Long = System.currentTimeMillis()
)

data class CheckoutForm(
    val name: String,
    val phone: String,
    val address: String,
    val deliveryMethod: String,
    val paymentMethod: String
)

interface ShopRepository {
    // catálogo
    fun observeProducts(): Flow<List<Product>>
    // carrito
    fun observeCart(): Flow<List<CartLine>>
    suspend fun addToCart(productId: String, qty: Int = 1)
    suspend fun setQty(productId: String, qty: Int)
    suspend fun removeFromCart(productId: String)
    suspend fun clearCart()

    // compra
    suspend fun checkout(form: CheckoutForm): OrderSummary
}
