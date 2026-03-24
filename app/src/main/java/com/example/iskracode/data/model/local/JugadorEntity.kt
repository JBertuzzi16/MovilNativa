package com.example.iskracode.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.iskracode.data.model.Jugador

@Entity(tableName = "jugador")
data class JugadorEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nombre: String,
    var saldoActual: Int) {
}