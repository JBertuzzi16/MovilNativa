package com.example.spin36.feature.juego


data class JuegoUiState(
    val nombreJugador: String = "",
    val saldoActual: Int = 50,
    val rachaActual: Int = 0,

    val tipoApuesta: String = "",
    val valorApuesta: String = "",
    val cantidadApuesta: String = "",

    val resultadoRuleta: Int? = null,
    val monedasGanadas: Int = 0,
    val bonusRacha: Int = 0,

    val mensajeResultado: String = "",
    val juegoTerminado: Boolean = false,

    val cargando: Boolean = false,
    val error: String? = null
)