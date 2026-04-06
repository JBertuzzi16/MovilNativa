package com.example.spin36.feature.historial


import androidx.lifecycle.ViewModel
import com.example.spin36.data.repository.CasinoRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistorialViewModel(
    private val repository: CasinoRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState

    fun cargarHistorialSesiones() {
        _uiState.value = _uiState.value.copy(
            cargando = true,
            error = null
        )

        val disposable = repository.obtenerHistorialSesiones()
            .subscribe({ listaSesiones ->
                _uiState.value = _uiState.value.copy(
                    sesiones = listaSesiones,
                    cargando = false,
                    error = null
                )
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = error.message ?: "Error al cargar el historial"
                )
            })

        disposables.add(disposable)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}