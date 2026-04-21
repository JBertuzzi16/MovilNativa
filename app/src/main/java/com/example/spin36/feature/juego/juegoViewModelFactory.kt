package com.example.spin36.feature.juego

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spin36.data.repository.CasinoRepository
import com.example.spin36.feature.galeria.GaleriaManager

class JuegoViewModelFactory(
    private val repository: CasinoRepository,
    private val galeriaManager: GaleriaManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JuegoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JuegoViewModel(repository, galeriaManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
