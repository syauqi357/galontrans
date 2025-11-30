package com.exam.galontrans.data.repo

import com.exam.galontrans.data.model.Product
import com.exam.galontrans.data.model.Transaction
import com.exam.galontrans.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalonRepository {

    private val api = RetrofitClient.apiService

    // ============ PRODUCTS ============

    suspend fun getAllProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch products: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(name: String, price: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val product = Product(name = name, price = price)
            val response = api.createProduct(product)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()?.message ?: "Product created")
            } else {
                Result.failure(Exception("Failed to create product: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: Int, name: String, price: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val product = Product(id = id, name = name, price = price)
            val response = api.updateProduct(id, product)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()?.message ?: "Product updated")
            } else {
                Result.failure(Exception("Failed to update product: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteProduct(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()?.message ?: "Product deleted")
            } else {
                Result.failure(Exception("Failed to delete product: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ TRANSACTIONS ============

    suspend fun getAllTransactions(): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllTransactions()
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
            val transactionData = mapOf(
                "product_id" to productId,
                "quantity" to quantity
            )
            val response = api.createTransaction(transactionData)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()?.message ?: "Transaction created")
            } else {
                Result.failure(Exception("Failed to create transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(id: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteTransaction(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()?.message ?: "Transaction deleted")
            } else {
                Result.failure(Exception("Failed to delete transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
