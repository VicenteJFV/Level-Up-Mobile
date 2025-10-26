package com.example.levelupmobile.domain.repo

import com.example.levelupmobile.domain.model.CartLine
import com.example.levelupmobile.domain.model.CheckoutForm
import com.example.levelupmobile.domain.model.OrderSummary
import com.example.levelupmobile.domain.model.Product
import kotlinx.coroutines.flow.Flow

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
