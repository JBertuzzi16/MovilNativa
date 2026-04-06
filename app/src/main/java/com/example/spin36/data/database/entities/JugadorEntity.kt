package com.example.spin36.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jugador")
data class JugadorEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "jugadorId")val jugadorId: Int = 0,
    @ColumnInfo(name = "nombre")val nombre: String,
    @ColumnInfo(name = "saldoActual")val saldoInicial: Int,
    @ColumnInfo (name = "rachaVictoria") val rachaInicial : Int
)