package com.example.levelupmobile.data.dao

import androidx.room.*
import com.example.levelupmobile.data.entity.CartLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // 🔹 Observa todo el carrito
    @Query("SELECT * FROM cart_lines")
    fun observeCart(): Flow<List<CartLineEntity>>

    // 🔹 Busca una línea específica del carrito
    @Query("SELECT * FROM cart_lines WHERE productId = :productId LIMIT 1")
    suspend fun findByProduct(productId: String): CartLineEntity?

    // 🔹 Inserta nueva línea
    @Insert
    suspend fun insert(line: CartLineEntity): Long

    // 🔹 Actualiza cantidad
    @Update
    suspend fun update(line: CartLineEntity)

    // 🔹 Elimina un producto
    @Query("DELETE FROM cart_lines WHERE productId = :productId")
    suspend fun deleteByProduct(productId: String)

    // 🔹 Vacía el carrito
    @Query("DELETE FROM cart_lines")
    suspend fun clear()
}
