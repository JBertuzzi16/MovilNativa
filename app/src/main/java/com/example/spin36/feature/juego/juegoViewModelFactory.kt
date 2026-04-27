package com.example.spin36.feature.juego

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spin36.data.repository.CasinoRepository
import com.example.spin36.feature.calendario.CalendarioManager
import com.example.spin36.feature.galeria.GaleriaManager
import com.example.spin36.feature.ubicacion.UbicacionManager

class JuegoViewModelFactory(
    private val repository: CasinoRepository,
    private val galeriaManager: GaleriaManager,
    private val calendarioManager: CalendarioManager,
    private val ubicacionManager: UbicacionManager,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JuegoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JuegoViewModel(repository, galeriaManager, calendarioManager,ubicacionManager,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
