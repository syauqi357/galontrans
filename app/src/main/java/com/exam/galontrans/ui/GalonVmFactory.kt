package com.exam.galontrans.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.exam.galontrans.data.remote.RetrofitClient

class GalonVmFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalonViewModel(RetrofitClient.apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}