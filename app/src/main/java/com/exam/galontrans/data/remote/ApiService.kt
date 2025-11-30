package com.exam.galontrans.data.remote

import com.exam.galontrans.data.model.ApiResponse
import com.exam.galontrans.data.model.Product
import com.exam.galontrans.data.model.Transaction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    // ============ PRODUCTS ============

    @GET("api.php?endpoint=products")
    suspend fun getAllProducts(): Response<List<Product>>

    @GET("api.php?endpoint=products")
    suspend fun getProduct(@Query("id") id: Int): Response<Product>

    @POST("api.php?endpoint=products")
    suspend fun createProduct(@Body product: Product): Response<ApiResponse<Product>>

    @PUT("api.php?endpoint=products")
    suspend fun updateProduct(
        @Query("id") id: Int,
        @Body product: Product
    ): Response<ApiResponse<Product>>

    @DELETE("api.php?endpoint=products")
    suspend fun deleteProduct(@Query("id") id: Int): Response<ApiResponse<Product>>

    // ============ TRANSACTIONS ============

    @GET("api.php?endpoint=transactions")
    suspend fun getAllTransactions(): Response<List<Transaction>>

    @GET("api.php?endpoint=transactions")
    suspend fun getTransaction(@Query("id") id: Int): Response<Transaction>

    @POST("api.php?endpoint=transactions")
    suspend fun createTransaction(@Body transaction: Map<String, Int>): Response<ApiResponse<Transaction>>

    @PUT("api.php?endpoint=transactions")
    suspend fun updateTransaction(
        @Query("id") id: Int,
        @Body transaction: Map<String, Int>
    ): Response<ApiResponse<Transaction>>

    @DELETE("api.php?endpoint=transactions")
    suspend fun deleteTransaction(@Query("id") id: Int): Response<ApiResponse<Transaction>>
}