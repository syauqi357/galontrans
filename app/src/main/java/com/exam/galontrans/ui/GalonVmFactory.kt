package com.exam.galontrans.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.exam.galontrans.data.repo.GalonRepository

class GalonVmFactory(private val repository: GalonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}