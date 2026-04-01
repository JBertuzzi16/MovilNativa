package com.example.spin36.data.repository

import com.example.spin36.data.database.dao.CasinoDAO
import com.example.spin36.data.database.entities.PartidaEntity
import com.example.spin36.data.mapper.toDomainSesion
import com.example.spin36.data.mapper.toEntity
import com.example.spin36.data.model.Jugador
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

class CasinoRepository(
    private val dao: CasinoDAO
) {

    fun insertarJugador(jugador: Jugador): Completable {
        return dao.insertarJugador(jugador.toEntity())
    }
    //creador juigador iniciial
    fun crearJugadorInicial(nombre: String = "Jugador"): io.reactivex.rxjava3.core.Completable {
        val jugador = Jugador(
            id = 0,
            nombre = nombre
        )
        return dao.insertarJugador(jugador.toEntity())
    }
    // Obtener jugador por nombre
    fun obtenerJugadorPorNombre (nombre: String):Maybe<Jugador>{
        return dao.obtenerJugadorPorNombre(nombre)
            .map { entidad -> Jugador(entidad.jugadorId, entidad.nombre, entidad.saldoInicial, entidad.rachaInicial) }
    }

    //aqui es donde reinicia el saldo actual con el mapper para inicio de sesion con 50 creditos
    fun obtenerJugadorSesion(): Maybe<Jugador> {
        return dao.obtenerJugadorActual()
            .map { jugadorEntity ->
                jugadorEntity.toDomainSesion()
            }
    }

    fun actualizarJugador(jugador: Jugador): Completable {
        return dao.actualizarJugador(jugador.toEntity())
    }

    fun insertarPartida(partida: PartidaEntity): Completable {
        return dao.insertarPartida(partida)
    }

    fun obtenerHistorial(): Single<List<PartidaEntity>> {
        return dao.obtenerHistorial()
    }
}