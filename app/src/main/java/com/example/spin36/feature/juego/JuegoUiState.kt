package com.example.spin36.feature.juego

import android.graphics.Bitmap

data class JuegoUiState(
    val nombreJugador: String = "",
    val saldoActual: Int = 50,
    val rachaActual: Int = 0,

    val tipoApuesta: String = "",
    val valorApuesta: String = "",
    val cantidadApuesta: String = "",

    val resultadoRuleta: Int? = null,
    val ganancia: Int = 0,
    val bonusRacha: Int = 0,

    val mensajeResultado: String = "",
    val juegoTerminado: Boolean = false,

    val cargando: Boolean = false,
    val error: String? = null,

    val bitmapVictoria: Bitmap? = null,
    val capturaPendiente: Boolean = false,
    val capturaGuardada: Boolean = false,
    val errorGuardado: String? = null,

    val animacionActiva: Boolean = false,
    val numeroAnimado: Int = 0,
    val animacionFinalizada: Boolean = false,
    val ultimaJugadaGanadora: Boolean = false,

    val victoriaEnCalendario: Boolean = false,
    val errorCalendario: String? = null
)
