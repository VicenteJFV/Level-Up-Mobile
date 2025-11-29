// File: `app/src/main/java/com/example/levelupmobile/data/api/CatalogApiClient.kt`
package com.example.levelupmobile.data.api

import com.example.levelupmobile.data.dto.ProductDto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface CatalogApi {
    @GET("/api/products")
    suspend fun getAllProducts(): Response<List<ProductDto>>

    @GET("/api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Response<ProductDto>
}

object CatalogRetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8081/"

    val api: CatalogApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogApi::class.java)
    }
}
