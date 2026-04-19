package com.example.spin36.feature.ajustes

data class AjustesUiState(
    val musicaActivada: Boolean = true,
    val uriMusicaPersonalizada: String? = null,
    val nombreMusicaPersonalizada: String? = null,
    val volumen: Float = 0.7f
)
