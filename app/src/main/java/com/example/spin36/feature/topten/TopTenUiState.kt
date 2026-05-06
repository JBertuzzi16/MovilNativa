package com.example.spin36.feature.topten

import com.example.spin36.data.remote.PuntuacionDto

data class TopTenUiState(
    val puntuaciones: List<PuntuacionDto> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)
