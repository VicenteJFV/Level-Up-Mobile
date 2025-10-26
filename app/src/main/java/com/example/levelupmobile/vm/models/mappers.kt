package com.example.levelupmobile.vm.models

import com.example.levelupmobile.domain.repo.Product

fun Product.toUi(): ProductUi = ProductUi(
    id = id,
    name = name,
    description = description,
    price = priceNeto,
    imageUrl = imageUrl
)
