package com.example.spin36.feature.juego

import android.graphics.BlurMaskFilter
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spin36.R
import com.example.spin36.feature.components.PantallaActual
import com.example.spin36.feature.components.Spin36TopBar
import com.example.spin36.feature.components.rememberSoundClick
import com.example.spin36.feature.historial.LogoSpin36
import com.example.spin36.feature.historial.fuenteRuleta
import com.example.spin36.ui.theme.casinoAntracitaSecundario
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoDoradoDetalles
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde
import kotlinx.coroutines.delay

private val fuenteRuleta = FontFamily(Font(R.font.mileast, FontWeight.Normal))

@Composable
fun JuegoScreen(
    viewModel: JuegoViewModel,
    onHistorialClick: () -> Unit,
    onMenuClick: () -> Unit,
    onAjustesClick: () -> Unit,
    onAyudaClick: () -> Unit = {},
    onSalirClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> if (concedido) viewModel.jugar() }

    val permisoCalendario = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    val permisoUbicacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    val guardarDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("image/webp")
    ) { uri ->
        if (uri != null) viewModel.guardarCapturaEnUri(uri) else viewModel.descartarCaptura()
    }

    fun onGirarConPermiso() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val permiso = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (context.checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED) {
                permisoLauncher.launch(permiso); return
            }
        }
        viewModel.jugar()
    }

    LaunchedEffect(Unit) {
        val permisos = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
        val faltaAlguno = permisos.any {
            context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (faltaAlguno) permisoCalendario.launch(permisos)
    }
    LaunchedEffect(uiState.victoriaEnCalendario) {
        if (uiState.victoriaEnCalendario){
            Toast.makeText(context, "¡Victoria guardada en el calendario!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val permisos = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        val faltaAlguno = permisos.any {
            context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (faltaAlguno) permisoUbicacion.launch(permisos)
    }
    JuegoContent(
        uiState                  = uiState,
        onCantidadApuestaChange  = { if (it.all { c -> c.isDigit() }) viewModel.onCantidadApuestaChange(it) },
        onSeleccionarPleno       = { viewModel.onTipoApuestaChange("pleno") },
        onSeleccionarDocena      = { viewModel.onTipoApuestaChange("docena") },
        onSeleccionarRojo        = { viewModel.onTipoApuestaChange("color"); viewModel.onValorApuestaChange("rojo") },
        onSeleccionarNegro       = { viewModel.onTipoApuestaChange("color"); viewModel.onValorApuestaChange("negro") },
        onSeleccionarPar         = { viewModel.onTipoApuestaChange("par_impar"); viewModel.onValorApuestaChange("par") },
        onSeleccionarImpar       = { viewModel.onTipoApuestaChange("par_impar"); viewModel.onValorApuestaChange("impar") },
        onSeleccionarNumeroPleno = { viewModel.onValorApuestaChange(it.toString()) },
        onSeleccionarDocenaValor = { viewModel.onValorApuestaChange(it.toString()) },
        onGirarClick             = { onGirarConPermiso() },
        onGuardarCapturaClick    = { guardarDocumentLauncher.launch("spin36_victoria_${System.currentTimeMillis()}.webp") },
        onDescartarCapturaClick  = { viewModel.descartarCaptura() },
        onConfirmarCalendario    = { viewModel.confirmarGuardadoEnCalendario() },
        onRechazarCalendario     = { viewModel.rechazarGuardadoEnCalendario() },
        onAnimacionFinalizada    = { viewModel.marcarAnimacionFinalizada() },
        onCerrarAnimacion        = { viewModel.cerrarAnimacion() },
        onHistorialClick         = onHistorialClick,
        onMenuClick              = onMenuClick,
        onAjustesClick           = onAjustesClick,
        onAyudaClick             = onAyudaClick,
        onSalirClick             = onSalirClick,
        onVolverClick            = onVolverClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuegoContent(
    uiState: JuegoUiState,
    onCantidadApuestaChange: (String) -> Unit,
    onSeleccionarPleno: () -> Unit,
    onSeleccionarDocena: () -> Unit,
    onSeleccionarRojo: () -> Unit,
    onSeleccionarNegro: () -> Unit,
    onSeleccionarPar: () -> Unit,
    onSeleccionarImpar: () -> Unit,
    onSeleccionarNumeroPleno: (Int) -> Unit,
    onSeleccionarDocenaValor: (Int) -> Unit,
    onGirarClick: () -> Unit,
    onGuardarCapturaClick: () -> Unit,
    onDescartarCapturaClick: () -> Unit,
    onConfirmarCalendario: () -> Unit,
    onRechazarCalendario: () -> Unit,
    onAnimacionFinalizada: () -> Unit,
    onCerrarAnimacion: () -> Unit,
    onHistorialClick: () -> Unit,
    onMenuClick: () -> Unit,
    onAjustesClick: () -> Unit,
    onAyudaClick: () -> Unit = {},
    onSalirClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = casinoVerde,
            topBar = {
                Spin36TopBar(
                    titulo            = "JUEGO",
                    pantallaActual    = PantallaActual.JUEGO,
                    onIrMenu          = onMenuClick,
                    onIrJuego         = {},
                    onIrHistorial     = onHistorialClick,
                    onIrAjustes       = onAjustesClick,
                    onIrAyuda         = onAyudaClick,
                    onSalirConfirmado = onSalirClick
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize().background(casinoVerde).padding(innerPadding)
            ) {
                ImagenRuleta(modifier = Modifier.align(Alignment.Center))
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ResumenJuegoItem(titulo = "Jugador", valor = uiState.nombreJugador, modifier = Modifier.weight(1f))
                        ResumenJuegoItem(titulo = "Saldo",   valor = "${uiState.saldoActual}", modifier = Modifier.weight(1f))
                        ResumenJuegoItem(titulo = "Racha",   valor = "${uiState.rachaActual}", modifier = Modifier.weight(1f))
                    }

                    OutlinedTextField(
                        value           = uiState.cantidadApuesta,
                        onValueChange   = onCantidadApuestaChange,
                        placeholder     = { Text(text = "Ingresa la apuesta...", fontFamily = fuenteRuleta, color = Color.Gray) },
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor        = casinoAntracitaSecundario,
                            unfocusedTextColor      = casinoAntracitaSecundario,
                            focusedContainerColor   = casinoBlanco,
                            unfocusedContainerColor = casinoBlanco,
                            focusedBorderColor      = casinoDoradoDetalles,
                            unfocusedBorderColor    = Color.Transparent
                        ),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "TIPO DE APUESTA", fontFamily = fuenteRuleta, fontSize = 22.sp, color = casinoBlanco)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TipoApuestaButton("PLENO x36", uiState.tipoApuesta == "pleno", onSeleccionarPleno, Modifier.weight(1f))
                        TipoApuestaButton("DOCENA x3", uiState.tipoApuesta == "docena", onSeleccionarDocena, Modifier.weight(1f))
                        TipoApuestaButton("ROJO x2",   uiState.tipoApuesta == "color" && uiState.valorApuesta == "rojo", onSeleccionarRojo, Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TipoApuestaButton("NEGRO x2",  uiState.tipoApuesta == "color" && uiState.valorApuesta == "negro", onSeleccionarNegro, Modifier.weight(1f))
                        TipoApuestaButton("PAR x2",    uiState.tipoApuesta == "par_impar" && uiState.valorApuesta == "par", onSeleccionarPar, Modifier.weight(1f))
                        TipoApuestaButton("IMPAR x2",  uiState.tipoApuesta == "par_impar" && uiState.valorApuesta == "impar", onSeleccionarImpar, Modifier.weight(1f))
                    }

                    when (uiState.tipoApuesta) {
                        "pleno" -> {
                            Text(text = "Elige número", fontFamily = fuenteRuleta, fontSize = 20.sp, color = casinoBlanco)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items((0..36).toList()) { numero ->
                                    ValorApuestaButton(numero.toString(), uiState.valorApuesta == numero.toString()) { onSeleccionarNumeroPleno(numero) }
                                }
                            }
                        }
                        "docena" -> {
                            Text(text = "Elige docena", fontFamily = fuenteRuleta, fontSize = 20.sp, color = casinoBlanco)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listOf(1, 2, 3)) { docena ->
                                    ValorApuestaButton("Docena $docena", uiState.valorApuesta == docena.toString()) { onSeleccionarDocenaValor(docena) }
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = casinoBlanco.copy(alpha = 0.35f))

                    if (uiState.cargando) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = casinoBlanco)
                        }
                    }
                    if (uiState.error != null) {
                        Text(text = uiState.error, color = casinoRojoAcciones, fontFamily = fuenteRuleta, fontSize = 18.sp)
                    }
                    if (uiState.resultadoRuleta != null || uiState.mensajeResultado.isNotBlank()) {
                        ResultadoPanel(uiState, onGuardarCapturaClick, onDescartarCapturaClick, onConfirmarCalendario, onRechazarCalendario)
                    }

                    Box(
                        modifier = Modifier
                            .dropShadow(shape = RoundedCornerShape(18.dp), shadow = Shadow(radius = 20.dp, color = casinoDoradoDetalles.copy(alpha = 0.35f), offset = DpOffset(0.dp, 16.dp)))
                            .background(brush = Brush.linearGradient(colors = listOf(casinoDoradoDetalles, casinoRojoAcciones)), shape = RoundedCornerShape(16.dp))
                            .padding(2.dp)
                    ) {
                        Button(
                            onClick  = rememberSoundClick(onGirarClick),
                            enabled  = !uiState.cargando && !uiState.juegoTerminado && !uiState.animacionActiva,
                            colors   = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
                            shape    = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().height(58.dp)
                        ) {
                            Text(text = "GIRAR", color = casinoBlanco, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 25.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        LogoSpin36(modifier = Modifier.fillMaxWidth(0.18f))
                        Box(modifier = Modifier.dropShadow(shape = RoundedCornerShape(10.dp), shadow = Shadow(radius = 20.dp, color = casinoDoradoDetalles.copy(alpha = 0.35f), offset = DpOffset(0.dp, 5.dp)))) {
                            OutlinedButton(
                                onClick = rememberSoundClick(onVolverClick),
                                colors  = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
                                shape   = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "Atrás", fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = casinoBlanco)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        if (uiState.animacionActiva) {
            RuletaAnimacionOverlay(
                numeroFinal           = uiState.numeroAnimado,
                esGanadora            = uiState.ultimaJugadaGanadora,
                animacionFinalizada   = uiState.animacionFinalizada,
                onAnimacionFinalizada = onAnimacionFinalizada,
                onToque               = onCerrarAnimacion
            )
        }

        if (uiState.juegoTerminado && !uiState.animacionActiva) {
            AlertDialog(
                onDismissRequest = {},
                title   = { Text(text = "Juego terminado", fontFamily = fuenteRuleta, fontSize = 24.sp) },
                text    = { Text(text = "Te has quedado sin saldo. ¿Qué quieres hacer?", fontFamily = fuenteRuleta, fontSize = 18.sp) },
                confirmButton = {
                    Button(
                        onClick = rememberSoundClick(onMenuClick),
                        colors  = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones)
                    ) { Text(text = "Volver al menú", fontFamily = fuenteRuleta, color = casinoBlanco) }
                },
                dismissButton = {
                    OutlinedButton(onClick = rememberSoundClick(onSalirClick)) {
                        Text(text = "Salir", fontFamily = fuenteRuleta)
                    }
                }
            )
        }
    }
}

@Composable
fun RuletaAnimacionOverlay(
    numeroFinal: Int,
    esGanadora: Boolean,
    animacionFinalizada: Boolean,
    onAnimacionFinalizada: () -> Unit,
    onToque: () -> Unit
) {
    val context = LocalContext.current
    var numeroVisible by remember { mutableIntStateOf((0..36).random()) }
    var rodando by remember { mutableStateOf(true) }

    val soundPool = remember {
        SoundPool.Builder().setMaxStreams(3).setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        ).build()
    }
    val soundId    = remember { soundPool.load(context, R.raw.tick_ruleta, 1) }
    val soundWinId = remember { soundPool.load(context, R.raw.win_sound,   1) }
    var streamIdVictoria by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            if (streamIdVictoria != 0) soundPool.stop(streamIdVictoria)
            soundPool.release()
        }
    }

    LaunchedEffect(Unit) {
        val intervalos = buildList {
            repeat(20) { add(70L) }; repeat(8) { add(110L) }
            repeat(5)  { add(180L) }; repeat(3) { add(280L) }; add(400L)
        }
        for (intervalo in intervalos) {
            numeroVisible = (0..36).random()
            soundPool.play(soundId, 0.8f, 0.8f, 1, 0, 1f)
            delay(intervalo)
        }
        numeroVisible = numeroFinal
        soundPool.play(soundId, 1f, 1f, 1, 0, 0.6f)
        rodando = false
        if (esGanadora) streamIdVictoria = soundPool.play(soundWinId, 1f, 1f, 1, -1, 1f)
        onAnimacionFinalizada()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = animacionFinalizada) { onToque() },
        contentAlignment = Alignment.Center
    ) {
        if (rodando) PanelNumeroRodando(numero = numeroVisible)
        else         PanelNumeroFinal(numero = numeroFinal, esGanadora = esGanadora)

        if (animacionFinalizada) {
            Text(
                text       = "Toca para continuar",
                color      = casinoBlanco.copy(alpha = 0.65f),
                fontFamily = fuenteRuleta,
                fontSize   = 16.sp,
                modifier   = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)
            )
        }
    }
}

@Composable
fun PanelNumeroRodando(numero: Int) {
    Box(
        modifier = Modifier.size(220.dp)
            .background(casinoBlanco, RoundedCornerShape(20.dp))
            .drawBehind { drawRoundRect(Color(0xFFC9A227), style = Stroke(4.dp.toPx()), cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx())) },
        contentAlignment = Alignment.Center
    ) {
        Text(text = numero.toString(), color = Color.Black, fontFamily = fuenteRuleta, fontSize = 96.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun PanelNumeroFinal(numero: Int, esGanadora: Boolean) {
    val transicion = rememberInfiniteTransition(label = "estrella")
    val rotacion by transicion.animateFloat(0f, 360f, infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart), label = "rotacion")
    val escala   by transicion.animateFloat(0.6f, 1.15f, infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "escala")

    Box(contentAlignment = Alignment.Center) {
        if (esGanadora) {
            Canvas(modifier = Modifier.size(320.dp)) {
                val cx = size.width / 2f; val cy = size.height / 2f
                val rExt = size.minDimension / 2f * escala; val rInt = rExt * 0.42f
                val path = androidx.compose.ui.graphics.Path().apply {
                    for (i in 0 until 16) {
                        val a = Math.toRadians((rotacion + i * 22.5).toDouble())
                        val r = if (i % 2 == 0) rExt else rInt
                        if (i == 0) moveTo(cx + (r * kotlin.math.cos(a)).toFloat(), cy + (r * kotlin.math.sin(a)).toFloat())
                        else         lineTo(cx + (r * kotlin.math.cos(a)).toFloat(), cy + (r * kotlin.math.sin(a)).toFloat())
                    }; close()
                }
                drawIntoCanvas { c ->
                    c.nativeCanvas.drawPath(path.asAndroidPath(), android.graphics.Paint().apply {
                        isAntiAlias = true; color = android.graphics.Color.argb(180, 201, 162, 39)
                        maskFilter  = BlurMaskFilter(48f, BlurMaskFilter.Blur.NORMAL)
                    })
                    c.nativeCanvas.drawPath(path.asAndroidPath(), android.graphics.Paint().apply {
                        isAntiAlias = true; color = android.graphics.Color.argb(90, 201, 162, 39)
                        maskFilter  = BlurMaskFilter(18f, BlurMaskFilter.Blur.NORMAL)
                        style = android.graphics.Paint.Style.STROKE; strokeWidth = 6f
                    })
                }
            }
        }

        Box(
            modifier = Modifier.size(220.dp)
                .background(casinoBlanco, RoundedCornerShape(20.dp))
                .drawBehind { drawRoundRect(Color(0xFFC9A227), style = Stroke(4.dp.toPx()), cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx())) }
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                Text(text = "Resultado", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(text = numero.toString(), color = if (esGanadora) casinoDoradoDetalles else Color.Black, fontFamily = fuenteRuleta, fontSize = 88.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun ResumenJuegoItem(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(color = casinoBlanco.copy(alpha = 0.12f), shape = RoundedCornerShape(14.dp)).padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, color = casinoBlanco.copy(alpha = 0.85f), fontSize = 14.sp, fontFamily = fuenteRuleta)
        Text(text = valor,  color = casinoBlanco, fontSize = 18.sp, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun TipoApuestaButton(texto: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val sonoro = rememberSoundClick(onClick)
    if (selected) {
        Button(onClick = sonoro, modifier = modifier.height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones), shape = RoundedCornerShape(14.dp)) {
            Text(text = texto, color = casinoBlanco, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
        }
    } else {
        OutlinedButton(onClick = sonoro, modifier = modifier.height(52.dp), shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, casinoBlanco)) {
            Text(text = texto, color = casinoBlanco, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ValorApuestaButton(texto: String, selected: Boolean, onClick: () -> Unit) {
    val sonoro = rememberSoundClick(onClick)
    if (selected) {
        Button(onClick = sonoro, colors = ButtonDefaults.buttonColors(containerColor = casinoDoradoDetalles), shape = RoundedCornerShape(14.dp)) {
            Text(text = texto, color = Color.Black, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(onClick = sonoro, shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, casinoBlanco)) {
            Text(text = texto, color = casinoBlanco, fontFamily = fuenteRuleta)
        }
    }
}

@Composable
fun ResultadoPanel(uiState: JuegoUiState, onGuardarCapturaClick: () -> Unit, onDescartarCapturaClick: () -> Unit, onConfirmarCalendario: () -> Unit, onRechazarCalendario: () -> Unit) {
    val sonoroGuardarCaptura      = rememberSoundClick(onGuardarCapturaClick)
    val sonoroDescartarCaptura    = rememberSoundClick(onDescartarCapturaClick)
    val sonoroConfirmarCalendario = rememberSoundClick(onConfirmarCalendario)
    val sonoroRechazarCalendario  = rememberSoundClick(onRechazarCalendario)

    Column(
        modifier = Modifier.fillMaxWidth().background(color = casinoBlanco.copy(alpha = 0.14f), shape = RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = "Resultado", color = casinoBlanco, fontFamily = fuenteRuleta, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        uiState.resultadoRuleta?.let { Text(text = "Número ganador: $it", color = casinoBlanco, fontFamily = fuenteRuleta, fontSize = 18.sp) }
        if (uiState.ganancia  > 0) Text(text = "Ganancia total: ${uiState.ganancia}", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 18.sp)
        if (uiState.bonusRacha > 0) Text(text = "Bonus racha: +${uiState.bonusRacha}", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 18.sp)
        if (uiState.mensajeResultado.isNotBlank()) Text(text = uiState.mensajeResultado, color = casinoBlanco, fontFamily = fuenteRuleta, fontSize = 17.sp)

        if (uiState.capturaPendiente) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "¿Guardar imagen de la victoria?", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = sonoroGuardarCaptura, colors = ButtonDefaults.buttonColors(containerColor = casinoDoradoDetalles), shape = RoundedCornerShape(10.dp)) {
                    Text(text = "Guardar", color = Color.Black, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = sonoroDescartarCaptura, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, casinoBlanco)) {
                    Text(text = "No guardar", color = casinoBlanco, fontFamily = fuenteRuleta)
                }
            }
        }
        if (uiState.capturaGuardada) Text(text = "¡Imagen guardada correctamente!", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 16.sp)
        if (uiState.errorGuardado != null) Text(text = uiState.errorGuardado, color = casinoRojoAcciones, fontFamily = fuenteRuleta, fontSize = 15.sp)

        if (uiState.victoriaCalendarioPendiente) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "¿Guardar victoria en el calendario?", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = sonoroConfirmarCalendario, colors = ButtonDefaults.buttonColors(containerColor = casinoDoradoDetalles), shape = RoundedCornerShape(10.dp)) {
                    Text(text = "Guardar", color = Color.Black, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = sonoroRechazarCalendario, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, casinoBlanco)) {
                    Text(text = "No guardar", color = casinoBlanco, fontFamily = fuenteRuleta)
                }
            }
        }
        if (uiState.victoriaEnCalendario) Text(text = "¡Victoria guardada en el calendario!", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 16.sp)
        if (uiState.errorCalendario != null) Text(text = uiState.errorCalendario, color = casinoRojoAcciones, fontFamily = fuenteRuleta, fontSize = 15.sp)
    }
}

@Composable
fun ImagenRuleta(modifier: Modifier = Modifier) {
    Image(painter = painterResource(id = R.drawable.ruleta), contentDescription = "Ruleta de fondo", modifier = modifier.size(550.dp), contentScale = ContentScale.Crop, alpha = 0.15f)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewJuegoContent() {
    JuegoContent(
        uiState = JuegoUiState(nombreJugador = "Carlos", saldoActual = 120, rachaActual = 2, tipoApuesta = "pleno", valorApuesta = "17", cantidadApuesta = "10", resultadoRuleta = 17, ganancia = 350, bonusRacha = 0, mensajeResultado = "Salió el 17. Has ganado 350 monedas."),
        onCantidadApuestaChange = {}, onSeleccionarPleno = {}, onSeleccionarDocena = {}, onSeleccionarRojo = {}, onSeleccionarNegro = {},
        onSeleccionarPar = {}, onSeleccionarImpar = {}, onSeleccionarNumeroPleno = {}, onSeleccionarDocenaValor = {},
        onGirarClick = {}, onGuardarCapturaClick = {}, onDescartarCapturaClick = {}, onConfirmarCalendario = {}, onRechazarCalendario = {}, onAnimacionFinalizada = {}, onCerrarAnimacion = {},
        onHistorialClick = {}, onMenuClick = {}, onAjustesClick = {}, onAyudaClick = {}, onSalirClick = {}, onVolverClick = {}
    )
}
