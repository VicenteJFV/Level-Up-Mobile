package com.example.levelupmobile.domain.repo

import com.example.levelupmobile.domain.model.CartLine
import com.example.levelupmobile.domain.model.CheckoutForm
import com.example.levelupmobile.domain.model.OrderSummary
import com.example.levelupmobile.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    //Catálogo
    fun observeProducts(): Flow<List<Product>>

    //lookup por ID (detalle)
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
}
