package com.example.spin36.feature.topten

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spin36.data.remote.TopTenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopTenViewModel : ViewModel() {

    private val repository = TopTenRepository()

    private val _uiState = MutableStateFlow(TopTenUiState())
    val uiState: StateFlow<TopTenUiState> = _uiState

    fun cargarTopTen() {
        _uiState.value = _uiState.value.copy(cargando = true, error = null)
        viewModelScope.launch {
            val lista = repository.obtenerTopTen()
            _uiState.value = _uiState.value.copy(
                puntuaciones = lista,
                cargando     = false,
                error        = if (lista.isEmpty()) "No hay puntuaciones aún" else null
            )
        }
    }
}
