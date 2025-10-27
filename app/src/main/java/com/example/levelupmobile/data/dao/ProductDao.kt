package com.example.levelupmobile.data.dao

import androidx.room.*
import com.example.levelupmobile.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    //Observa todos los productos (Flow para UI reactiva)
    @Query("SELECT * FROM products")
    fun observeAll(): Flow<List<ProductEntity>>

    //Observa un producto por ID
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ProductEntity?>

    //Inserta o actualiza uno o varios productos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(product: ProductEntity)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ProductEntity?
}
