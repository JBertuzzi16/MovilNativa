package com.example.spin36.feature.juego

import androidx.lifecycle.ViewModel
import com.example.spin36.data.database.entities.PartidaEntity
import com.example.spin36.data.database.entities.SesionEntity
import com.example.spin36.data.model.Apuesta
import com.example.spin36.data.model.Jugador
import com.example.spin36.data.model.Sesion
import com.example.spin36.data.repository.CasinoRepository
import com.example.spin36.feature.galeria.GaleriaManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JuegoViewModel(
    private val repository: CasinoRepository,
    private val galeriaManager: GaleriaManager? = null
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _uiState = MutableStateFlow(JuegoUiState())
    val uiState: StateFlow<JuegoUiState> = _uiState

    private var jugadorActual: Jugador? = null
    private var sesionActual: SesionEntity? = null

    fun cargarJugador(nombreRecibido: String) {
        if (jugadorActual?.nombre==nombreRecibido){
            return
        }
        _uiState.value = _uiState.value.copy(
            cargando = true,
            error = null
        )

        val disposable = repository.obtenerJugadorPorNombre(nombreRecibido)
            .switchIfEmpty(
                repository.crearJugadorInicial(nombreRecibido)
                    .andThen(repository.obtenerJugadorPorNombre(nombreRecibido))
            )
            .toSingle()
            .flatMap { jugador ->

                jugador.reiniciarSesion()

                repository.actualizarJugador(jugador)
                    .andThen(
                        repository.crearSesion(
                            nombreJugador = jugador.nombre,
                            fechaHoraInicio = obtenerFechaHoraActual(),
                            saldoInicial = jugador.saldoActual
                        )
                    )
                    .map { sesionCreada ->
                        Pair(jugador, sesionCreada)
                    }
            }
            .subscribe({ (jugador, sesionCreada) ->
                jugadorActual = jugador
                sesionActual = sesionCreada

                _uiState.value = _uiState.value.copy(
                    nombreJugador = jugador.nombre,
                    saldoActual = jugador.saldoActual,
                    rachaActual = jugador.rachaDeVictorias,
                    resultadoRuleta = null,
                    ganancia = 0,
                    bonusRacha = 0,
                    mensajeResultado = "",
                    juegoTerminado = false,
                    cargando = false,
                    error = null
                )
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = error.message ?: "Error al cargar o crear el jugador"
                )
            })

        disposables.add(disposable)
    }

    fun onTipoApuestaChange(tipo: String) {
        _uiState.value = _uiState.value.copy(
            tipoApuesta = tipo,
            valorApuesta = "",
            error = null
        )
    }

    fun onValorApuestaChange(valor: String) {
        _uiState.value = _uiState.value.copy(
            valorApuesta = valor,
            error = null
        )
    }

    fun onCantidadApuestaChange(cantidad: String) {
        _uiState.value = _uiState.value.copy(
            cantidadApuesta = cantidad,
            error = null
        )
    }

    fun jugar() {
        val jugador = jugadorActual ?: run {
            _uiState.value = _uiState.value.copy(
                error = "No hay jugador cargado"
            )
            return
        }

        val sesionBase = sesionActual ?: run {
            _uiState.value = _uiState.value.copy(
                error = "No hay sesión activa"
            )
            return
        }

        val state = _uiState.value
        val cantidad = state.cantidadApuesta.toIntOrNull()

        if (state.tipoApuesta.isBlank()) {
            _uiState.value = state.copy(error = "Selecciona un tipo de apuesta")
            return
        }

        if (state.valorApuesta.isBlank()) {
            _uiState.value = state.copy(error = "Introduce un valor de apuesta")
            return
        }

        if (cantidad == null || cantidad <= 0) {
            _uiState.value = state.copy(error = "La cantidad apostada no es válida")
            return
        }

        if (!jugador.tieneSaldoSuficiente(cantidad)) {
            _uiState.value = state.copy(error = "No tienes saldo suficiente")
            return
        }

        val apuesta = crearApuesta(
            tipo = state.tipoApuesta,
            valor = state.valorApuesta,
            cantidad = cantidad
        )

        if (apuesta == null) {
            _uiState.value = state.copy(error = "Datos de apuesta no válidos")
            return
        }

        jugador.actualizarSaldo(-cantidad)

        val numeroGanador = Sesion.girar()
        val premio = apuesta.calcularPremio(numeroGanador)
        val haGanado = premio > 0

        if (haGanado) {
            jugador.actualizarSaldo(premio)
        }

        val bonusActivado = jugador.gestionRacha(haGanado)
        val bonusRacha = if (bonusActivado) 100 else 0
        val rachaDeEstaJugada = if (bonusActivado) 5 else jugador.rachaDeVictorias
        val monedasGanadasTotales = premio + bonusRacha

        val juegoTerminado = jugador.saldoActual <= 0

        val partida = PartidaEntity(
            sesionId = sesionBase.sesionId,
            jugadorId = jugador.id,
            fechaHora = obtenerFechaHoraActual(),
            tipoApuesta = construirDescripcionApuesta(state.tipoApuesta, state.valorApuesta),
            montoApostado = cantidad,
            numeroGanador = numeroGanador,
            resultado = if (haGanado) "Ganado" else "Perdido",
            racha = rachaDeEstaJugada,
            monedasGanadas = monedasGanadasTotales
        )

        val sesionActualizada = sesionBase.copy(
            saldoFinal = jugador.saldoActual,
            rachaMaxima = maxOf(sesionBase.rachaMaxima, rachaDeEstaJugada),
            apuestasRealizadas = sesionBase.apuestasRealizadas + 1,
            fechaHoraFin = if (juegoTerminado) obtenerFechaHoraActual() else sesionBase.fechaHoraFin
        )

        val mensaje = construirMensajeResultado(
            numeroGanador = numeroGanador,
            premio = premio,
            bonusRacha = bonusRacha
        )

        guardarJugadorPartidaYSesion(
            jugador = jugador,
            partida = partida,
            sesionActualizada = sesionActualizada,
            numeroGanador = numeroGanador,
            monedasGanadas = monedasGanadasTotales,
            bonusRacha = bonusRacha,
            mensaje = mensaje,
            juegoTerminado = juegoTerminado
        )
    }

    private fun guardarJugadorPartidaYSesion(
        jugador: Jugador,
        partida: PartidaEntity,
        sesionActualizada: SesionEntity,
        numeroGanador: Int,
        monedasGanadas: Int,
        bonusRacha: Int,
        mensaje: String,
        juegoTerminado: Boolean
    ) {
        _uiState.value = _uiState.value.copy(
            cargando = true,
            error = null
        )

        val disposable = repository.actualizarJugador(jugador)
            .andThen(repository.insertarPartida(partida))
            .andThen(repository.actualizarSesion(sesionActualizada))
            .subscribe({
                sesionActual = sesionActualizada

                val (capturaGuardada, errorGaleria) = intentarGuardarCaptura(
                    monedasGanadas = monedasGanadas,
                    nombreJugador  = jugador.nombre,
                    numeroGanador  = numeroGanador,
                    fecha          = obtenerFechaHoraActual()
                )

                _uiState.value = _uiState.value.copy(
                    saldoActual      = jugador.saldoActual,
                    rachaActual      = jugador.rachaDeVictorias,
                    resultadoRuleta  = numeroGanador,
                    ganancia         = monedasGanadas,
                    bonusRacha       = bonusRacha,
                    mensajeResultado = mensaje,
                    juegoTerminado   = juegoTerminado,
                    capturaGuardada  = capturaGuardada,
                    errorGaleria     = errorGaleria,
                    cargando         = false,
                    error            = null
                )
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = error.message ?: "Error al guardar la jugada"
                )
            })

        disposables.add(disposable)
    }

    fun cerrarSesionActual(onSesionCerrada: () -> Unit) {
        val jugador = jugadorActual ?: return
        val sesion = sesionActual ?: return

        val sesionCerrada = sesion.copy(
            saldoFinal = jugador.saldoActual,
            rachaMaxima = maxOf(sesion.rachaMaxima, jugador.rachaDeVictorias),
            fechaHoraFin = obtenerFechaHoraActual()
        )

        val disposable = repository.actualizarSesion(sesionCerrada)
            .subscribe({
                sesionActual = sesionCerrada
                jugadorActual = null
                sesionActual = null
                onSesionCerrada()
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Error al cerrar la sesión"
                )
            })

        disposables.add(disposable)
    }
    private fun crearApuesta(tipo: String, valor: String, cantidad: Int): Apuesta? {
        return when (tipo.lowercase()) {
            "pleno" -> {
                val numero = valor.toIntOrNull() ?: return null
                if (numero !in 0..36) return null
                Apuesta.Pleno(numero, cantidad)
            }

            "color" -> {
                val color = valor.lowercase()
                if (color != "rojo" && color != "negro") return null
                Apuesta.Color(color, cantidad)
            }

            "docena" -> {
                val docena = valor.toIntOrNull() ?: return null
                if (docena !in 1..3) return null
                Apuesta.Docena(docena, cantidad)
            }

            "par_impar" -> {
                val tipoParidad = valor.lowercase()
                if (tipoParidad != "par" && tipoParidad != "impar") return null
                Apuesta.ParImpar(tipoParidad, cantidad)
            }

            else -> null
        }
    }

    private fun construirDescripcionApuesta(tipo: String, valor: String): String {
        return "$tipo: $valor"
    }

    private fun construirMensajeResultado(
        numeroGanador: Int,
        premio: Int,
        bonusRacha: Int
    ): String {
        return when {
            premio > 0 && bonusRacha > 0 ->
                "Ha salido el $numeroGanador. Has ganado $premio monedas y bonus de racha +$bonusRacha."

            premio > 0 ->
                "Ha salido el $numeroGanador. Has ganado $premio monedas."

            else ->
                "Ha salido el $numeroGanador. Has perdido la apuesta."
        }
    }

    private fun obtenerFechaHoraActual(): String {
        return SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
    }

    private fun intentarGuardarCaptura(
        monedasGanadas: Int,
        nombreJugador: String,
        numeroGanador: Int,
        fecha: String
    ): Pair<Boolean, String?> {
        if (monedasGanadas <= 0 || galeriaManager == null) return Pair(false, null)
        return try {
            galeriaManager.guardarVictoria(nombreJugador, numeroGanador, monedasGanadas, fecha)
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, "No se pudo guardar la captura: ${e.message}")
        }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}