package com.example.spin36.feature.juego

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spin36.R
import com.example.spin36.feature.calendario.CalendarioManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.spin36.data.database.entities.PartidaEntity
import com.example.spin36.data.database.entities.SesionEntity
import com.example.spin36.data.model.Apuesta
import com.example.spin36.data.model.Jugador
import com.example.spin36.data.model.Sesion
import com.example.spin36.data.repository.CasinoRepository
import com.example.spin36.feature.galeria.GaleriaManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JuegoViewModel(
    private val repository: CasinoRepository,
    private val galeriaManager: GaleriaManager? = null,
    private val calendarioManager: CalendarioManager? = null,
    context: Context? = null
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _uiState = MutableStateFlow(JuegoUiState())
    val uiState: StateFlow<JuegoUiState> = _uiState

    private var jugadorActual: Jugador? = null
    private var sesionActual: SesionEntity? = null

    private val soundPool: SoundPool? = context?.let {
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            ).build()
    }
    private val soundCapturaId: Int = context?.let { soundPool?.load(it, R.raw.captura_sound, 1) } ?: 0

    fun cargarJugador(nombreRecibido: String) {
        if (jugadorActual?.nombre == nombreRecibido) return

        _uiState.value = _uiState.value.copy(cargando = true, error = null)

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
                            nombreJugador   = jugador.nombre,
                            fechaHoraInicio = obtenerFechaHoraActual(),
                            saldoInicial    = jugador.saldoActual
                        )
                    )
                    .map { sesionCreada -> Pair(jugador, sesionCreada) }
            }
            .subscribe({ (jugador, sesionCreada) ->
                jugadorActual = jugador
                sesionActual  = sesionCreada
                _uiState.value = _uiState.value.copy(
                    nombreJugador    = jugador.nombre,
                    saldoActual      = jugador.saldoActual,
                    rachaActual      = jugador.rachaDeVictorias,
                    resultadoRuleta  = null,
                    ganancia         = 0,
                    bonusRacha       = 0,
                    mensajeResultado = "",
                    juegoTerminado   = false,
                    bitmapVictoria   = null,
                    capturaPendiente = false,
                    capturaGuardada  = false,
                    errorGuardado    = null,
                    cargando         = false,
                    error            = null
                )
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error    = error.message ?: "Error al cargar o crear el jugador"
                )
            })

        disposables.add(disposable)
    }

    fun onTipoApuestaChange(tipo: String) {
        _uiState.value = _uiState.value.copy(tipoApuesta = tipo, valorApuesta = "", error = null)
    }

    fun onValorApuestaChange(valor: String) {
        _uiState.value = _uiState.value.copy(valorApuesta = valor, error = null)
    }

    fun onCantidadApuestaChange(cantidad: String) {
        _uiState.value = _uiState.value.copy(cantidadApuesta = cantidad, error = null)
    }

    fun jugar() {
        val jugador    = jugadorActual ?: run { _uiState.value = _uiState.value.copy(error = "No hay jugador cargado"); return }
        val sesionBase = sesionActual  ?: run { _uiState.value = _uiState.value.copy(error = "No hay sesión activa");  return }

        val state    = _uiState.value
        val cantidad = state.cantidadApuesta.toIntOrNull()

        if (state.tipoApuesta.isBlank())             { _uiState.value = state.copy(error = "Selecciona un tipo de apuesta");      return }
        if (state.valorApuesta.isBlank())             { _uiState.value = state.copy(error = "Introduce un valor de apuesta");     return }
        if (cantidad == null || cantidad <= 0)        { _uiState.value = state.copy(error = "La cantidad apostada no es válida"); return }
        if (!jugador.tieneSaldoSuficiente(cantidad))  { _uiState.value = state.copy(error = "No tienes saldo suficiente");        return }

        val apuesta = crearApuesta(state.tipoApuesta, state.valorApuesta, cantidad)
            ?: run { _uiState.value = state.copy(error = "Datos de apuesta no válidos"); return }

        jugador.actualizarSaldo(-cantidad)

        val numeroGanador         = Sesion.girar()
        val premio                = apuesta.calcularPremio(numeroGanador)
        val haGanado              = premio > 0
        if (haGanado) jugador.actualizarSaldo(premio)

        val bonusActivado         = jugador.gestionRacha(haGanado)
        val bonusRacha            = if (bonusActivado) 100 else 0
        val rachaDeEstaJugada     = if (bonusActivado) 5 else jugador.rachaDeVictorias
        val monedasGanadasTotales = premio + bonusRacha
        val juegoTerminado        = jugador.saldoActual <= 0

        val partida = PartidaEntity(
            sesionId       = sesionBase.sesionId,
            jugadorId      = jugador.id,
            fechaHora      = obtenerFechaHoraActual(),
            tipoApuesta    = construirDescripcionApuesta(state.tipoApuesta, state.valorApuesta),
            montoApostado  = cantidad,
            numeroGanador  = numeroGanador,
            resultado      = if (haGanado) "Ganado" else "Perdido",
            racha          = rachaDeEstaJugada,
            monedasGanadas = monedasGanadasTotales
        )

        val sesionActualizada = sesionBase.copy(
            saldoFinal         = jugador.saldoActual,
            rachaMaxima        = maxOf(sesionBase.rachaMaxima, rachaDeEstaJugada),
            apuestasRealizadas = sesionBase.apuestasRealizadas + 1,
            fechaHoraFin       = if (juegoTerminado) obtenerFechaHoraActual() else sesionBase.fechaHoraFin
        )

        guardarJugadorPartidaYSesion(
            jugador           = jugador,
            partida           = partida,
            sesionActualizada = sesionActualizada,
            numeroGanador     = numeroGanador,
            monedasGanadas    = monedasGanadasTotales,
            bonusRacha        = bonusRacha,
            mensaje           = construirMensajeResultado(numeroGanador, premio, bonusRacha),
            juegoTerminado    = juegoTerminado,
            haGanado          = haGanado
        )

        _uiState.value = _uiState.value.copy(
            animacionActiva      = true,
            animacionFinalizada  = false,
            numeroAnimado        = numeroGanador,
            ultimaJugadaGanadora = haGanado
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
        juegoTerminado: Boolean,
        haGanado: Boolean
    ) {
        _uiState.value = _uiState.value.copy(cargando = true, error = null)

        val disposable = repository.actualizarJugador(jugador)
            .andThen(repository.insertarPartida(partida))
            .andThen(repository.actualizarSesion(sesionActualizada))
            .subscribe({
                sesionActual = sesionActualizada


                val bitmap = if (haGanado && galeriaManager != null) {
                    galeriaManager.crearBitmapVictoria(
                        nombreJugador = jugador.nombre,
                        numeroGanador = numeroGanador,
                        fecha = obtenerFechaHoraActual(),
                        tipoApuesta = partida.tipoApuesta,
                        cantidadApostada = partida.montoApostado,
                        montoGanado = partida.monedasGanadas
                    )
                } else null

                if (haGanado) {
                    guardarVictoriaEnCalendario(
                        tipoApuesta   = partida.tipoApuesta,
                        numeroGanador = numeroGanador,
                        montoGanado   = partida.monedasGanadas
                    )
                }

                _uiState.value = _uiState.value.copy(
                    saldoActual      = jugador.saldoActual,
                    rachaActual      = jugador.rachaDeVictorias,
                    resultadoRuleta  = numeroGanador,
                    ganancia         = monedasGanadas,
                    bonusRacha       = bonusRacha,
                    mensajeResultado = mensaje,
                    juegoTerminado   = juegoTerminado,
                    bitmapVictoria   = bitmap,
                    capturaPendiente = bitmap != null,
                    capturaGuardada  = false,
                    errorGuardado    = null,
                    cargando         = false,
                    error            = null,
                    victoriaEnCalendario = false,
                    errorCalendario = null,
                )
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error    = error.message ?: "Error al guardar la jugada"
                )
            })

        disposables.add(disposable)
    }

    fun marcarAnimacionFinalizada() {
        _uiState.value = _uiState.value.copy(animacionFinalizada = true)
    }

    fun cerrarAnimacion() {
        _uiState.value = _uiState.value.copy(
            animacionActiva     = false,
            animacionFinalizada = false
        )
    }

    fun guardarCapturaEnUri(uri: Uri) {
        val bitmap  = _uiState.value.bitmapVictoria ?: return
        val manager = galeriaManager ?: return

        val disposable = Completable.fromCallable { manager.guardarBitmapEnUri(bitmap, uri) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (soundCapturaId != 0) soundPool?.play(soundCapturaId, 1f, 1f, 1, 0, 1f)
                _uiState.value = _uiState.value.copy(
                    capturaPendiente = false,
                    capturaGuardada  = true,
                    bitmapVictoria   = null,
                    errorGuardado    = null
                )
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    errorGuardado = "No se pudo guardar la imagen: ${error.message}"
                )
            })

        disposables.add(disposable)
    }

    fun descartarCaptura() {
        _uiState.value = _uiState.value.copy(
            bitmapVictoria   = null,
            capturaPendiente = false,
            capturaGuardada  = false,
            errorGuardado    = null
        )
    }

    fun guardarVictoriaEnCalendario(tipoApuesta: String, numeroGanador: Int, montoGanado: Int) {
        val manager = calendarioManager ?: return
        val jugador = jugadorActual ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val exito = try {
                manager.guardarVictoriaEnCalendario(
                    nombreJugador = jugador.nombre,
                    tipoApuesta   = tipoApuesta,
                    numeroGanador = numeroGanador,
                    montoGanado   = montoGanado,
                    fechaMillis   = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                false
            }
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    victoriaEnCalendario = exito,
                    errorCalendario = if (exito) null else "No se pudo guardar en el calendario"
                )
            }
        }
    }

    fun cerrarSesionActual(onSesionCerrada: () -> Unit) {
        val jugador = jugadorActual ?: return
        val sesion  = sesionActual  ?: return

        val sesionCerrada = sesion.copy(
            saldoFinal   = jugador.saldoActual,
            rachaMaxima  = maxOf(sesion.rachaMaxima, jugador.rachaDeVictorias),
            fechaHoraFin = obtenerFechaHoraActual()
        )

        val disposable = repository.actualizarSesion(sesionCerrada)
            .subscribe({
                sesionActual  = sesionCerrada
                jugadorActual = null
                sesionActual  = null
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

    private fun construirDescripcionApuesta(tipo: String, valor: String) = "$tipo: $valor"

    private fun construirMensajeResultado(numeroGanador: Int, premio: Int, bonusRacha: Int): String {
        return when {
            premio > 0 && bonusRacha > 0 -> "Ha salido el $numeroGanador. Has ganado $premio monedas y bonus de racha +$bonusRacha."
            premio > 0                   -> "Ha salido el $numeroGanador. Has ganado $premio monedas."
            else                         -> "Ha salido el $numeroGanador. Has perdido la apuesta."
        }
    }

    private fun obtenerFechaHoraActual(): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

    override fun onCleared() {
        soundPool?.release()
        disposables.clear()
        super.onCleared()
    }
}
