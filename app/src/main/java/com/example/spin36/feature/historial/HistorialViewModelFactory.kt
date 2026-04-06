package com.example.spin36.feature.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spin36.data.repository.CasinoRepository

class HistorialViewModelFactory(
    private val repository: CasinoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistorialViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistorialViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}