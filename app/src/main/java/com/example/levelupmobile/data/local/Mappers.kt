package com.example.levelupmobile.data.local

import com.example.levelupmobile.data.entity.*
import com.example.levelupmobile.domain.model.CartLine
import com.example.levelupmobile.domain.model.Product

fun ProductEntity.toDomain(): Product =
    Product(
        id = id,
        name = name,
        description = description,
        priceNeto = priceNeto,
        ivaRate = ivaRate,
        imageUrl = imageUrl,
        stock = stock,
        categoryId = categoryId
    )


fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    description = description,
    priceNeto = priceNeto,
    ivaRate = ivaRate,
    imageUrl = imageUrl,
    stock = stock,
    categoryId = categoryId
)

fun CartLineEntity.toDomain() = CartLine(productId = productId, qty = qty)
fun CartLine.toEntity() = CartLineEntity(productId = productId, qty = qty)
