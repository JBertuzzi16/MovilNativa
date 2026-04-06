package com.example.spin36.feature.historial

import com.example.spin36.data.database.entities.SesionEntity

data class HistorialUiState(
    val sesiones: List<SesionEntity> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)