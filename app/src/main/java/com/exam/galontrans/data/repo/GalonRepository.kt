package com.exam.galontrans.data.repo

import com.exam.galontrans.data.model.CreateTransactionRequest
import com.exam.galontrans.data.model.Product
import com.exam.galontrans.data.model.Transaction
import com.exam.galontrans.data.model.UpdateStockRequest
import com.exam.galontrans.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalonRepository {

    private val apiService = RetrofitClient.apiService

    // ========== PRODUCTS ==========

    suspend fun getAllProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch products: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Int): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProductById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch product: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStock(productId: Int, delta: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateStockRequest(delta)
            val response = apiService.updateStock(productId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Failed to update stock: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== TRANSACTIONS ==========

    suspend fun getAllTransactions(): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllTransactions()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch transactions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTransaction(productId: Int, quantity: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = CreateTransactionRequest(productId, quantity)
            val response = apiService.createTransaction(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to create transaction"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(id: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteTransaction(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Failed to delete transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
