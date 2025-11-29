package com.exam.galontrans.data.repo

import com.exam.galontrans.data.dao.ProductDao
import com.exam.galontrans.data.dao.TransactionDao
import com.exam.galontrans.data.entity.Product
import com.exam.galontrans.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

class GalonRepository(
    private val productDao: ProductDao,
    private val transactionDao: TransactionDao
) {
    // Product operations
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    suspend fun insertProduct(product: Product) {
        productDao.insert(product)
    }

    suspend fun updateProduct(product: Product) {
        productDao.update(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.delete(product)
    }

    // Transaction operations
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }
}