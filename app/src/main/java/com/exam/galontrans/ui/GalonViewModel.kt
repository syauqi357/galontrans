package com.exam.galontrans.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.galontrans.data.model.Product
import com.exam.galontrans.data.model.Transaction
import com.exam.galontrans.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GalonViewModel(private val apiService: ApiService) : ViewModel() {

    // State flows for UI
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Computed values
    val totalSales: StateFlow<Int> = transactions
        .map { transactionList -> transactionList.sumOf { it.totalPrice } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val transactionCount: StateFlow<Int> = transactions
        .map { transactionList -> transactionList.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    init {
        loadTransactions()
        loadProducts()
    }

    /**
     * Load transactions from API
     */
    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = apiService.getAllTransactions()
                if (response.isSuccessful) {
                    _transactions.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat data: ${response.message()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
                _transactions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load products from API
     */
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.getAllProducts()
                if (response.isSuccessful) {
                    _products.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat produk: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
                _products.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Add a new transaction
     */
    fun addTransaction(productId: Int, quantity: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val transactionData = mapOf("product_id" to productId, "quantity" to quantity)
                val response = apiService.createTransaction(transactionData)
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Transaksi berhasil!"
                    onResult(message)
                    loadTransactions() // Refresh transactions
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    onResult("Gagal menambah transaksi: $errorBody")
                }
            } catch (e: Exception) {
                onResult("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up any resources if needed
    }
}