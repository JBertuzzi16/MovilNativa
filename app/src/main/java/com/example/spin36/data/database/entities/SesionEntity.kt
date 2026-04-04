package com.example.spin36.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_sesiones")
    data class SesionEntity(
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "sesionId")
    val sesionId: Int = 0,

    @ColumnInfo(name = "nombreJugador")
    val nombreJugador: String,

    @ColumnInfo(name = "fechaHoraInicio")
    val fechaHoraInicio: String,

    @ColumnInfo(name = "fechaHoraFin")
    val fechaHoraFin: String? = null,

    @ColumnInfo(name = "saldoFinal")
    val saldoFinal: Int,

    @ColumnInfo(name = "rachaMaxima")
    val rachaMaxima: Int,

    @ColumnInfo(name = "apuestasRealizadas")
    val apuestasRealizadas: Int
)
