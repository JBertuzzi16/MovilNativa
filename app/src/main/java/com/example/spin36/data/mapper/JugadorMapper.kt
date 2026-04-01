package com.example.spin36.data.mapper

import com.example.spin36.data.database.entities.JugadorEntity
import com.example.spin36.data.model.Jugador


//de esta manera inicia con el saldo a 50 y no el saldoActual almacenado en Room
fun JugadorEntity.toDomainSesion(): Jugador {
    return Jugador(
        id = jugadorId,
        nombre = nombre
    )
}
//deja listo el jugador actual para almacenarlo por entity creando un jugadorentity
fun Jugador.toEntity(): JugadorEntity {
    return JugadorEntity(
        jugadorId = id,
        nombre = nombre,
        saldoInicial = saldoActual,
        rachaInicial = rachaDeVictorias
    )
}