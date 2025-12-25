package com.exam.galontrans.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.galontrans.data.model.Product
import com.exam.galontrans.data.model.Transaction
import com.exam.galontrans.data.remote.ApiService
import com.exam.galontrans.data.repo.GalonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GalonUiState(
    val products: List<Product> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val purchaseSuccess: Boolean = false
)
class GalonViewModel(private val repository: GalonRepository) : ViewModel() {

    private val _uiState = mutableStateOf(GalonUiState())
    val uiState: State<GalonUiState> = _uiState

    init {
        loadProducts()
        loadTransactions()
    }

    // ========== PRODUCTS ==========

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getAllProducts()
                .onSuccess { productList ->
                    _uiState.value = _uiState.value.copy(
                        products = productList,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message,
                        isLoading = false
                    )
                }
        }
    }

    fun getAvailableProducts(): List<Product> {
        return _uiState.value.products.filter { it.stock > 0 }
    }

    // ========== TRANSACTIONS ==========

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getAllTransactions()
                .onSuccess { transactionList ->
                    _uiState.value = _uiState.value.copy(
                        transactions = transactionList,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message,
                        isLoading = false
                    )
                }
        }
    }

    fun createTransaction(productId: Int, quantity: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.createTransaction(productId, quantity)
                .onSuccess { message ->
                    _uiState.value = _uiState.value.copy(
                        successMessage = message,
                        purchaseSuccess = true,
                        isLoading = false
                    )
                    // Reload data
                    loadProducts()
                    loadTransactions()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message,
                        purchaseSuccess = false,
                        isLoading = false
                    )
                }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.deleteTransaction(id)
                .onSuccess { message ->
                    _uiState.value = _uiState.value.copy(
                        successMessage = message,
                        isLoading = false
                    )
                    // Reload data
                    loadProducts()
                    loadTransactions()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message,
                        isLoading = false
                    )
                }
        }
    }

    fun getTotalRevenue(): Double {
        return _uiState.value.transactions.sumOf { it.getTotalPrice() }
    }

    fun getFormattedTotalRevenue(): String {
        return "Rp ${String.format("%,.0f", getTotalRevenue())}"
    }

    // ========== UI HELPERS ==========

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun resetPurchaseSuccess() {
        _uiState.value = _uiState.value.copy(purchaseSuccess = false)
    }
}