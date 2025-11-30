package com.exam.galontrans.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.galontrans.data.model.Product
import com.exam.galontrans.data.model.Transaction
import com.exam.galontrans.data.repo.GalonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GalonViewModel : ViewModel() {

    private val repository = GalonRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _totalSales = MutableStateFlow(0)
    val totalSales: StateFlow<Int> = _totalSales

    private val _transactionCount = MutableStateFlow(0)
    val transactionCount: StateFlow<Int> = _transactionCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadProducts()
        loadTransactions()
    }

    // ============ PRODUCTS ============

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllProducts()
                .onSuccess { _products.value = it }
                .onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun addProduct(name: String, price: Int, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createProduct(name, price)
                .onSuccess {
                    onSuccess(it)
                    loadProducts()
                }
                .onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateProduct(id: Int, name: String, price: Int, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateProduct(id, name, price)
                .onSuccess {
                    onSuccess(it)
                    loadProducts()
                }
                .onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteProduct(id: Int, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteProduct(id)
                .onSuccess {
                    onSuccess(it)
                    loadProducts()
                }
                .onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    // ============ TRANSACTIONS ============

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllTransactions()
                .onSuccess {
                    _transactions.value = it
                    _totalSales.value = it.sumOf { trans -> trans.totalPrice }
                    _transactionCount.value = it.size
                }
                .onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun addTransaction(productId: Int, quantity: Int, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createTransaction(productId, quantity)
                .onSuccess {
                    onSuccess(it)
                    loadTransactions()
                }
                .onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteTransaction(id: Int, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteTransaction(id)
                .onSuccess {
                    onSuccess(it)
                    loadTransactions()
                }
                .onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
