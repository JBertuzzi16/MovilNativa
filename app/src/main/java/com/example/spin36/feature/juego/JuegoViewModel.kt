package com.example.spin36.feature.juego

import androidx.lifecycle.ViewModel
import com.example.spin36.data.database.entities.PartidaEntity
import com.example.spin36.data.model.Apuesta
import com.example.spin36.data.model.Jugador
import com.example.spin36.data.model.Sesion
import com.example.spin36.data.repository.CasinoRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JuegoViewModel(
    private val repository: CasinoRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _uiState = MutableStateFlow(JuegoUiState())
    val uiState: StateFlow<JuegoUiState> = _uiState

    private var jugadorActual: Jugador? = null

    fun cargarJugador() {
        _uiState.value = _uiState.value.copy(
            cargando = true,
            error = null
        )

        val disposable = repository.obtenerJugadorSesion()
            .switchIfEmpty(
                repository.crearJugadorInicial("Jugador")//instanciua jugador inicial
                    .andThen(repository.obtenerJugadorSesion())
            )
            .subscribe({ jugador ->
                jugadorActual = jugador

                _uiState.value = _uiState.value.copy(
                    nombreJugador = jugador.nombre,
                    saldoActual = jugador.saldoActual,
                    rachaActual = jugador.rachaDeVictorias,
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
        val rachaParaHistorial = if (bonusActivado) 5 else jugador.rachaDeVictorias
        val monedasGanadasTotales = premio + bonusRacha

        val  fechaHora = SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        val partida = PartidaEntity(
            jugadorId = jugador.id,
            fechaHora = fechaHora,
            tipoApuesta = construirDescripcionApuesta(state.tipoApuesta, state.valorApuesta),
            montoApostado = cantidad,
            numeroGanador = numeroGanador,
            resultado = if (haGanado) "Ganado" else "Perdido",
            racha = rachaParaHistorial,
            monedasGanadas = monedasGanadasTotales
        )

        val mensaje = construirMensajeResultado(
            numeroGanador = numeroGanador,
            premio = premio,
            bonusRacha = bonusRacha
        )

        val juegoTerminado = jugador.saldoActual <= 0

        guardarJugadorYPartida(
            jugador = jugador,
            partida = partida,
            numeroGanador = numeroGanador,
            monedasGanadas = monedasGanadasTotales,
            bonusRacha = bonusRacha,
            mensaje = mensaje,
            juegoTerminado = juegoTerminado
        )
    }

    private fun guardarJugadorYPartida(
        jugador: Jugador,
        partida: PartidaEntity,
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
            .subscribe({
                _uiState.value = _uiState.value.copy(
                    saldoActual = jugador.saldoActual,
                    rachaActual = jugador.rachaDeVictorias,
                    resultadoRuleta = numeroGanador,
                    monedasGanadas = monedasGanadas,
                    bonusRacha = bonusRacha,
                    mensajeResultado = mensaje,
                    juegoTerminado = juegoTerminado,
                    cargando = false,
                    error = null
                )
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = error.message ?: "Error al guardar la partida"
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
                "Salió el $numeroGanador. Has ganado $premio monedas y bonus de racha +$bonusRacha."

            premio > 0 ->
                "Salió el $numeroGanador. Has ganado $premio monedas."

            else ->
                "Salió el $numeroGanador. Has perdido la apuesta."
        }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}