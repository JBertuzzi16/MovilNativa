package com.example.spin36.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.spin36.data.database.entities.JugadorEntity
import com.example.spin36.data.database.entities.PartidaEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface CasinoDAO {

    @Insert
    fun insertarJugador (jugadorEntity: JugadorEntity): Completable

    @Insert
    fun insertarPartida (partida: PartidaEntity): Completable

    // Recupera el saldo del jugador
    @Query("SELECT * FROM jugador LIMIT 1")
    fun obtenerJugadorActual(): Maybe<JugadorEntity>

    // Buscar jugador por nombre
    @Query ( "SELECT * FROM jugador WHERE nombre = :nombre LIMIT 1")
    fun obtenerJugadorPorNombre (nombre:String): Maybe<JugadorEntity>

    // Recupera el historial
    @Query(value = "SELECT * FROM historial_partidas ORDER BY partidaId DESC")
    fun obtenerHistorial(): Single<List<PartidaEntity>>

    // Actualiza saldo del jugador
    @Update
    fun actualizarJugador(jugador: JugadorEntity): Completable
}