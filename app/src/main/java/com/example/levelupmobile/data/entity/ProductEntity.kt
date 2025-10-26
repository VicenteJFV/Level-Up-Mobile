package com.example.levelupmobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val priceNeto: Long,
    val ivaRate: Double = 0.19,
    val imageUrl: String? = null,
    val stock: Int = 999,
    val categoryId: Int? = null
)
