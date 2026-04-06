package com.example.spin36.feature.bienvenida

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class BienvenidaViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(BienvenidaUiState())
    val uiState: StateFlow<BienvenidaUiState> = _uiState.asStateFlow()

    fun onNombreChange (nuevoNombre: String) {
        _uiState.value = _uiState.value.copy(
            nombre = nuevoNombre,
            error = null
        )
    }
}