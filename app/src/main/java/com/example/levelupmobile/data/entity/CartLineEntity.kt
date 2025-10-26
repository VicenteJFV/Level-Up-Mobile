package com.example.levelupmobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_lines")
data class CartLineEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
    val productId: String,
    val qty: Int
)
