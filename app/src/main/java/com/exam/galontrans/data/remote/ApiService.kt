package com.exam.galontrans.data.remote

import com.exam.galontrans.data.model.ApiResponse
import com.exam.galontrans.data.model.CreateTransactionRequest
import com.exam.galontrans.data.model.Product
import com.exam.galontrans.data.model.Transaction
import com.exam.galontrans.data.model.UpdateStockRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ========== PRODUCTS ==========

    @GET("products")
    suspend fun getAllProducts(): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<Product>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<ApiResponse>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: Product
    ): Response<ApiResponse>

    @PATCH("products/{id}/stock")
    suspend fun updateStock(
        @Path("id") id: Int,
        @Body request: UpdateStockRequest
    ): Response<ApiResponse>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<ApiResponse>

    // ========== TRANSACTIONS ==========

    @GET("transactions")
    suspend fun getAllTransactions(): Response<List<Transaction>>

    @GET("transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: Int): Response<Transaction>

    @POST("transactions")
    suspend fun createTransaction(
        @Body request: CreateTransactionRequest
    ): Response<ApiResponse>

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: Int,
        @Body request: CreateTransactionRequest
    ): Response<ApiResponse>

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Int): Response<ApiResponse>
}