package com.example.spin36.feature.juego

import com.example.spin36.data.remote.PuntuacionDto
import com.example.spin36.data.remote.TopTenRepository
import com.example.spin36.data.remote.PREMIO_TOP_TEN
import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import androidx.annotation.RequiresPermission
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
import com.example.spin36.feature.notificacion.NotificacionHelper
import com.example.spin36.feature.ubicacion.UbicacionManager
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
    private val ubicacionManager: UbicacionManager? = null,
    context: Context? = null
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val appContext: Context? = context?.applicationContext
    private val topTenRepository = TopTenRepository()

    private val _uiState = MutableStateFlow(JuegoUiState())
    val uiState: StateFlow<JuegoUiState> = _uiState

    private var jugadorActual: Jugador? = null
    private var sesionActual: SesionEntity? = null
    private var pendienteTipoApuesta: String? = null
    private var pendienteNumeroGanador: Int? = null
    private var pendienteMontoGanado: Int? = null

    private var tiempoInicioJugada: Long = 0

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

    private val notificacionHelper : NotificacionHelper? = context?.let { NotificacionHelper(it) }
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

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
   fun jugar() {
        val jugador    = jugadorActual ?: run { _uiState.value = _uiState.value.copy(error = "No hay jugador cargado"); return }
        val sesionBase = sesionActual  ?: run { _uiState.value = _uiState.value.copy(error = "No hay sesión activa");  return }

        val state    = _uiState.value
        val cantidad = state.cantidadApuesta.toIntOrNull()

        if (state.tipoApuesta.isBlank())             { _uiState.value = state.copy(error = "Selecciona un tipo de apuesta");      return }
        if (state.valorApuesta.isBlank())             { _uiState.value = state.copy(error = "Introduce un valor de apuesta");     return }
        if (cantidad == null || cantidad <= 0)        { _uiState.value = state.copy(error = "La cantidad apostada no es válida"); return }
        if (!jugador.tieneSaldoSuficiente(cantidad))  { _uiState.value = state.copy(error = "No tienes saldo suficiente");        return }

        tiempoInicioJugada = System.currentTimeMillis()

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
        // Lo lento lo pasamos a una couritine
        viewModelScope.launch(Dispatchers.IO){
        val ubicacion = ubicacionManager?.obtenerUbicacion()
        val partida = PartidaEntity(
            sesionId       = sesionBase.sesionId,
            jugadorId      = jugador.id,
            fechaHora      = obtenerFechaHoraActual(),
            tipoApuesta    = construirDescripcionApuesta(state.tipoApuesta, state.valorApuesta),
            montoApostado  = cantidad,
            numeroGanador  = numeroGanador,
            resultado      = if (haGanado) "Ganado" else "Perdido",
            racha          = rachaDeEstaJugada,
            monedasGanadas = monedasGanadasTotales,
            latitud        = ubicacion?.first,
            longitud       = ubicacion?.second,
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
        }

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
                        nombreJugador      = jugador.nombre,
                        numeroGanador      = numeroGanador,
                        fecha              = obtenerFechaHoraActual(),
                        tipoApuesta        = partida.tipoApuesta,
                        cantidadApostada   = partida.montoApostado,
                        montoGanado        = partida.monedasGanadas,
                        textoVictoria      = appContext?.getString(R.string.bitmap_victoria)       ?: "¡VICTORIA!",
                        textoNumeroGanador = appContext?.getString(R.string.bitmap_numero_ganador, numeroGanador) ?: "Número ganador: $numeroGanador",
                        textoApuesta       = appContext?.getString(R.string.bitmap_apuesta, partida.tipoApuesta)  ?: "Apuesta: ${partida.tipoApuesta}",
                        textoCantidad      = appContext?.getString(R.string.bitmap_cantidad, partida.montoApostado) ?: "Cantidad apostada: ${partida.montoApostado} monedas",
                        textoMonto         = appContext?.getString(R.string.bitmap_monto, partida.monedasGanadas)   ?: "Monto ganado: ${partida.monedasGanadas} monedas"
                    )
                } else null

                if (haGanado) {
                    pendienteTipoApuesta   = partida.tipoApuesta
                    pendienteNumeroGanador = numeroGanador
                    pendienteMontoGanado   = partida.monedasGanadas
                    val tiempoResolucion = System.currentTimeMillis() - tiempoInicioJugada
                    notificacionHelper?.mostrarNotificacionVictoria(tiempoResolucion)
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
                    victoriaCalendarioPendiente = haGanado,
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
        val manager = calendarioManager ?: run {
            _uiState.value = _uiState.value.copy(
                errorCalendario = appContext?.getString(R.string.calendario_error) ?: "No se pudo guardar en el calendario"
            )
            return
        }
        val jugador = jugadorActual ?: return

        viewModelScope.launch(Dispatchers.IO) {
            var errorDetalle: String? = null
            val exito = try {
                manager.guardarVictoriaEnCalendario(
                    nombreJugador = jugador.nombre,
                    tipoApuesta   = tipoApuesta,
                    numeroGanador = numeroGanador,
                    montoGanado   = montoGanado,
                    fechaMillis   = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                errorDetalle = e.message
                false
            }
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    victoriaEnCalendario = exito,
                    errorCalendario = when {
                        exito        -> null
                        errorDetalle != null -> "${appContext?.getString(R.string.calendario_error)}: $errorDetalle"
                        else         -> appContext?.getString(R.string.calendario_error) ?: "No se pudo guardar en el calendario"
                    }
                )
            }
        }
    }

    fun confirmarGuardadoEnCalendario() {
        val tipo   = pendienteTipoApuesta   ?: return
        val numero = pendienteNumeroGanador ?: return
        val monto  = pendienteMontoGanado   ?: return
        guardarVictoriaEnCalendario(tipo, numero, monto)
        _uiState.value = _uiState.value.copy(victoriaCalendarioPendiente = false)
    }

    fun rechazarGuardadoEnCalendario() {
        _uiState.value = _uiState.value.copy(victoriaCalendarioPendiente = false)
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
                val uid       = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: jugador.nombre
                val nombre    = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.displayName ?: jugador.nombre

                viewModelScope.launch(Dispatchers.IO) {
                    val puntuacion = PuntuacionDto(
                        nombre     = nombre,
                        puntuacion = sesionCerrada.saldoFinal,
                        fecha      = obtenerFechaHoraActual(),
                        uid        = uid
                    )
                    val entroEnTop = topTenRepository.intentarEntrarEnTopTen(puntuacion)
                    withContext(Dispatchers.Main) {
                        if (entroEnTop) {
                            jugador.actualizarSaldo(PREMIO_TOP_TEN)
                            _uiState.value = _uiState.value.copy(
                                saldoActual = jugador.saldoActual
                            )
                        }
                        jugadorActual = null
                        sesionActual  = null
                        onSesionCerrada()
                    }
                }
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
            premio > 0 && bonusRacha > 0 -> appContext?.getString(R.string.msg_ganado_bonus, numeroGanador, premio, bonusRacha)
                ?: "Ha salido el $numeroGanador. Has ganado $premio monedas y bonus de racha +$bonusRacha."
            premio > 0 -> appContext?.getString(R.string.msg_ganado, numeroGanador, premio)
                ?: "Ha salido el $numeroGanador. Has ganado $premio monedas."
            else -> appContext?.getString(R.string.msg_perdido, numeroGanador)
                ?: "Ha salido el $numeroGanador. Has perdido la apuesta."
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
