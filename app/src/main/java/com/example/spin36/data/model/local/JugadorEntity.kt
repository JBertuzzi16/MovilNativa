package com.example.spin36.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jugador")
data class JugadorEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nombre: String,
    var saldoActual: Int) {
}