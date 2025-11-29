package com.example.levelupmobile.domain.repo

import com.example.levelupmobile.data.dto.OrderResponse
import com.example.levelupmobile.domain.model.CartLine
import com.example.levelupmobile.domain.model.CheckoutForm
import com.example.levelupmobile.domain.model.OrderSummary
import com.example.levelupmobile.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    // Catálogo
    fun observeProducts(): Flow<List<Product>>

    // Lookup por ID (detalle)
    suspend fun getById(id: String): Product?
    fun observeById(id: String): Flow<Product?>

    // Carrito
    fun observeCart(): Flow<List<CartLine>>
    suspend fun addToCart(productId: String, qty: Int = 1)
    suspend fun setQty(productId: String, qty: Int)
    suspend fun removeFromCart(productId: String)
    suspend fun clearCart()

    // Compra
    suspend fun checkout(form: CheckoutForm): OrderSummary

    // Gestión de órdenes
    suspend fun getOrder(orderId: Long): OrderResponse?
    suspend fun updateOrder(orderId: Long, phone: String, address: String): OrderResponse?
    suspend fun confirmOrder(orderId: Long): OrderResponse?
    suspend fun cancelOrder(orderId: Long): Boolean
}