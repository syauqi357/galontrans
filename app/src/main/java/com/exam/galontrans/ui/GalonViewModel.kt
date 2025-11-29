package com.exam.galontrans.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.galontrans.data.entity.Product
import com.exam.galontrans.data.entity.Transaction
import com.exam.galontrans.data.repo.GalonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GalonViewModel(private val repository: GalonRepository) : ViewModel() {

    val products: Flow<List<Product>> = repository.allProducts
    val transactions: Flow<List<Transaction>> = repository.allTransactions

    // Product operations (MASTER)
    fun addProduct(name: String, price: Int) {
        viewModelScope.launch {
            repository.insertProduct(Product(name = name, price = price))
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // Transaction operations
    fun addTransaction(product: Product, quantity: Int) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            val transaction = Transaction(
                productId = product.id,
                productName = product.name,
                quantity = quantity,
                totalPrice = product.price * quantity,
                date = dateFormat.format(Date())
            )
            repository.insertTransaction(transaction)
        }
    }
}
