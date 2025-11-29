// File: `app/src/main/java/com/example/levelupmobile/data/local/Mappers.kt`
package com.example.levelupmobile.data.local

import com.example.levelupmobile.data.entity.*
import com.example.levelupmobile.data.dto.ProductDto
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

fun ProductDto.toDomain(): Product =
    Product(
        id = id.toString(),
        name = name,
        description = description ?: "",
        priceNeto = price.toLong(),
        ivaRate = 0.19,
        imageUrl = imageUrl,
        stock = stock ?: 0,
        categoryId = null
    )

fun List<ProductDto>.toDomainList(): List<Product> = map { it.toDomain() }
