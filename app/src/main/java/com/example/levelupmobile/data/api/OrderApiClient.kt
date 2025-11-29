package com.example.levelupmobile.data.api

import com.example.levelupmobile.data.dto.OrderRequest
import com.example.levelupmobile.data.dto.OrderResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface OrderApi {
    @POST("/api/orders")
    suspend fun createOrder(@Body order: OrderRequest): Response<OrderResponse>

    @GET("/api/orders/{id}")
    suspend fun getOrder(@Path("id") id: Long): Response<OrderResponse>

    @PUT("/api/orders/{id}")
    suspend fun updateOrder(@Path("id") id: Long, @Body order: OrderRequest): Response<OrderResponse>

    @PATCH("/api/orders/{id}/confirm")
    suspend fun confirmOrder(@Path("id") id: Long): Response<OrderResponse>

    @DELETE("/api/orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Long): Response<Void>
}

object OrderRetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8082/"

    val api: OrderApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OrderApi::class.java)
    }
}