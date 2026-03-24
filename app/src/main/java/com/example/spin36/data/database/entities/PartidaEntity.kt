package com.example.spin36.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_partidas")
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "partidaId")val partidaId: Int = 0,
    @ColumnInfo(name ="jugadorId" )val jugadorId: Int,
    @ColumnInfo(name ="tipoApuesta" )val tipoApuesta: String,
    @ColumnInfo(name ="resultadoRuleta" )val resultadoRuleta : Int,
    @ColumnInfo(name ="ganancia" )val ganancia : Double
)