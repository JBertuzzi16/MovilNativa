package com.example.spin36.feature.juego

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spin36.R
import com.example.spin36.feature.components.PantallaActual
import com.example.spin36.feature.components.Spin36TopBar
import com.example.spin36.feature.historial.LogoSpin36
import com.example.spin36.feature.historial.fuenteRuleta
import com.example.spin36.ui.theme.casinoAntracitaSecundario
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoDoradoDetalles
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde

private val fuenteRuleta = FontFamily(
    Font(R.font.mileast, FontWeight.Normal)
)

@Composable
fun JuegoScreen(
    viewModel: JuegoViewModel,
    onHistorialClick: () -> Unit,
    onMenuClick: () -> Unit,
    onAjustesClick: () -> Unit,
    onSalirClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) viewModel.jugar()
    }

    val guardarDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("image/webp")
    ) { uri ->
        if (uri != null) viewModel.guardarCapturaEnUri(uri)
        else viewModel.descartarCaptura()
    }

    fun onGirarConPermiso() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val permiso = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (context.checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED) {
                permisoLauncher.launch(permiso)
                return
            }
        }
        viewModel.jugar()
    }

    JuegoContent(
        uiState = uiState,
        onCantidadApuestaChange = { nuevoTexto ->
            if (nuevoTexto.all { it.isDigit() }) {
                viewModel.onCantidadApuestaChange(nuevoTexto)
            }
        },
        onSeleccionarPleno   = { viewModel.onTipoApuestaChange("pleno") },
        onSeleccionarDocena  = { viewModel.onTipoApuestaChange("docena") },
        onSeleccionarRojo    = {
            viewModel.onTipoApuestaChange("color")
            viewModel.onValorApuestaChange("rojo")
        },
        onSeleccionarNegro   = {
            viewModel.onTipoApuestaChange("color")
            viewModel.onValorApuestaChange("negro")
        },
        onSeleccionarPar     = {
            viewModel.onTipoApuestaChange("par_impar")
            viewModel.onValorApuestaChange("par")
        },
        onSeleccionarImpar   = {
            viewModel.onTipoApuestaChange("par_impar")
            viewModel.onValorApuestaChange("impar")
        },
        onSeleccionarNumeroPleno = { numero -> viewModel.onValorApuestaChange(numero.toString()) },
        onSeleccionarDocenaValor = { docena -> viewModel.onValorApuestaChange(docena.toString()) },
        onGirarClick             = { onGirarConPermiso() },
        onGuardarCapturaClick    = {
            guardarDocumentLauncher.launch("spin36_victoria_${System.currentTimeMillis()}.webp")
        },
        onDescartarCapturaClick  = { viewModel.descartarCaptura() },
        onHistorialClick         = onHistorialClick,
        onMenuClick              = onMenuClick,
        onAjustesClick           = onAjustesClick,
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
    onHistorialClick: () -> Unit,
    onMenuClick: () -> Unit,
    onAjustesClick: () -> Unit,
    onSalirClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    Scaffold(
        containerColor = casinoVerde,
        topBar = {
            Spin36TopBar(
                titulo         = "JUEGO",
                pantallaActual = PantallaActual.JUEGO,
                onIrMenu       = onMenuClick,
                onIrJuego      = {},
                onIrHistorial  = onHistorialClick,
                onIrAjustes    = onAjustesClick,
                onSalirConfirmado = onSalirClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(casinoVerde)
                .padding(innerPadding)
        ) {
            ImagenRuleta(modifier = Modifier.align(Alignment.Center))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //resumen globañl
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResumenJuegoItem(titulo = "Jugador", valor = uiState.nombreJugador, modifier = Modifier.weight(1f))
                    ResumenJuegoItem(titulo = "Saldo",   valor = "${uiState.saldoActual}", modifier = Modifier.weight(1f))
                    ResumenJuegoItem(titulo = "Racha",   valor = "${uiState.rachaActual}", modifier = Modifier.weight(1f))
                }

                //cantidad apuesta
                OutlinedTextField(
                    value = uiState.cantidadApuesta,
                    onValueChange = onCantidadApuestaChange,
                    placeholder = {
                        Text(text = "Ingresa la apuesta...", fontFamily = fuenteRuleta, color = Color.Gray)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor     = casinoAntracitaSecundario,
                        unfocusedTextColor   = casinoAntracitaSecundario,
                        focusedContainerColor   = casinoBlanco,
                        unfocusedContainerColor = casinoBlanco,
                        focusedBorderColor   = casinoDoradoDetalles,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape    = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                //tipo de apuesta
                Text(text = "TIPO DE APUESTA", fontFamily = fuenteRuleta, fontSize = 22.sp, color = casinoBlanco)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoApuestaButton(texto = "PLENO x36", selected = uiState.tipoApuesta == "pleno",  onClick = onSeleccionarPleno,  modifier = Modifier.weight(1f))
                    TipoApuestaButton(texto = "DOCENA x3", selected = uiState.tipoApuesta == "docena", onClick = onSeleccionarDocena, modifier = Modifier.weight(1f))
                    TipoApuestaButton(texto = "ROJO x2",   selected = uiState.tipoApuesta == "color" && uiState.valorApuesta == "rojo",  onClick = onSeleccionarRojo,  modifier = Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoApuestaButton(texto = "NEGRO x2",  selected = uiState.tipoApuesta == "color" && uiState.valorApuesta == "negro", onClick = onSeleccionarNegro, modifier = Modifier.weight(1f))
                    TipoApuestaButton(texto = "PAR x2",    selected = uiState.tipoApuesta == "par_impar" && uiState.valorApuesta == "par",   onClick = onSeleccionarPar,   modifier = Modifier.weight(1f))
                    TipoApuestaButton(texto = "IMPAR x2",  selected = uiState.tipoApuesta == "par_impar" && uiState.valorApuesta == "impar", onClick = onSeleccionarImpar, modifier = Modifier.weight(1f))
                }

                //elegir valor
                when (uiState.tipoApuesta) {
                    "pleno" -> {
                        Text(text = "Elige número", fontFamily = fuenteRuleta, fontSize = 20.sp, color = casinoBlanco)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items((0..36).toList()) { numero ->
                                ValorApuestaButton(
                                    texto    = numero.toString(),
                                    selected = uiState.valorApuesta == numero.toString(),
                                    onClick  = { onSeleccionarNumeroPleno(numero) }
                                )
                            }
                        }
                    }
                    "docena" -> {
                        Text(text = "Elige docena", fontFamily = fuenteRuleta, fontSize = 20.sp, color = casinoBlanco)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf(1, 2, 3)) { docena ->
                                ValorApuestaButton(
                                    texto    = "Docena $docena",
                                    selected = uiState.valorApuesta == docena.toString(),
                                    onClick  = { onSeleccionarDocenaValor(docena) }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = casinoBlanco.copy(alpha = 0.35f))

                //estados de error, cargando y resultado
                if (uiState.cargando) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(color = casinoBlanco)
                    }
                }

                if (uiState.error != null) {
                    Text(text = uiState.error, color = casinoRojoAcciones, fontFamily = fuenteRuleta, fontSize = 18.sp)
                }

                if (uiState.resultadoRuleta != null || uiState.mensajeResultado.isNotBlank()) {
                    ResultadoPanel(
                        uiState                 = uiState,
                        onGuardarCapturaClick   = onGuardarCapturaClick,
                        onDescartarCapturaClick = onDescartarCapturaClick
                    )
                }

                //boton girar
                Box(
                    modifier = Modifier
                        .dropShadow(
                            shape  = RoundedCornerShape(18.dp),
                            shadow = Shadow(radius = 20.dp, color = casinoDoradoDetalles.copy(alpha = 0.35f), offset = DpOffset(0.dp, 16.dp))
                        )
                        .background(
                            brush = Brush.linearGradient(colors = listOf(casinoDoradoDetalles, casinoRojoAcciones)),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(2.dp)
                ) {
                    Button(
                        onClick  = onGirarClick,
                        enabled  = !uiState.cargando && !uiState.juegoTerminado,
                        colors   = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(58.dp)
                    ) {
                        Text(text = "GIRAR", color = casinoBlanco, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 25.sp)
                    }
                }

                //logo + atras
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    LogoSpin36(modifier = Modifier.fillMaxWidth(0.18f))
                    Box(
                        modifier = Modifier.dropShadow(
                            shape  = RoundedCornerShape(10.dp),
                            shadow = Shadow(radius = 20.dp, color = casinoDoradoDetalles.copy(alpha = 0.35f), offset = DpOffset(0.dp, 5.dp))
                        )
                    ) {
                        OutlinedButton(
                            onClick = onVolverClick,
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

    if (uiState.juegoTerminado) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = "Juego terminado", fontFamily = fuenteRuleta, fontSize = 24.sp)
            },
            text = {
                Text(
                    text = "Te has quedado sin saldo. ¿Qué quieres hacer?",
                    fontFamily = fuenteRuleta,
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onMenuClick,
                    colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones)
                ) {
                    Text(text = "Volver al menú", fontFamily = fuenteRuleta, color = casinoBlanco)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onSalirClick) {
                    Text(text = "Salir", fontFamily = fuenteRuleta)
                }
            }
        )
    }
}


@Composable
fun ResumenJuegoItem(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color = casinoBlanco.copy(alpha = 0.12f), shape = RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, color = casinoBlanco.copy(alpha = 0.85f), fontSize = 14.sp, fontFamily = fuenteRuleta)
        Text(text = valor,  color = casinoBlanco, fontSize = 18.sp, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun TipoApuestaButton(texto: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (selected) {
        Button(onClick = onClick, modifier = modifier.height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones), shape = RoundedCornerShape(14.dp)) {
            Text(text = texto, color = casinoBlanco, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier.height(52.dp), shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, casinoBlanco)) {
            Text(text = texto, color = casinoBlanco, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ValorApuestaButton(texto: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = casinoDoradoDetalles), shape = RoundedCornerShape(14.dp)) {
            Text(text = texto, color = Color.Black, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(onClick = onClick, shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, casinoBlanco)) {
            Text(text = texto, color = casinoBlanco, fontFamily = fuenteRuleta)
        }
    }
}

@Composable
fun ResultadoPanel(
    uiState: JuegoUiState,
    onGuardarCapturaClick: () -> Unit,
    onDescartarCapturaClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = casinoBlanco.copy(alpha = 0.14f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = "Resultado", color = casinoBlanco, fontFamily = fuenteRuleta, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        uiState.resultadoRuleta?.let { numero ->
            Text(text = "Número ganador: $numero", color = casinoBlanco, fontFamily = fuenteRuleta, fontSize = 18.sp)
        }
        if (uiState.ganancia > 0) {
            Text(text = "Ganancia total: ${uiState.ganancia}", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 18.sp)
        }
        if (uiState.bonusRacha > 0) {
            Text(text = "Bonus racha: +${uiState.bonusRacha}", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 18.sp)
        }
        if (uiState.mensajeResultado.isNotBlank()) {
            Text(text = uiState.mensajeResultado, color = casinoBlanco, fontFamily = fuenteRuleta, fontSize = 17.sp)
        }

        if (uiState.capturaPendiente) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "¿Guardar imagen de la victoria?", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onGuardarCapturaClick,
                    colors  = ButtonDefaults.buttonColors(containerColor = casinoDoradoDetalles),
                    shape   = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Guardar", color = Color.Black, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onDescartarCapturaClick,
                    shape   = RoundedCornerShape(10.dp),
                    border  = BorderStroke(1.dp, casinoBlanco)
                ) {
                    Text(text = "No guardar", color = casinoBlanco, fontFamily = fuenteRuleta)
                }
            }
        }

        if (uiState.capturaGuardada) {
            Text(text = "¡Imagen guardada correctamente!", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontSize = 16.sp)
        }
        if (uiState.errorGuardado != null) {
            Text(text = uiState.errorGuardado, color = casinoRojoAcciones, fontFamily = fuenteRuleta, fontSize = 15.sp)
        }
    }
}

@Composable
fun ImagenRuleta(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ruleta),
        contentDescription = "Ruleta de fondo",
        modifier = modifier.size(550.dp),
        contentScale = ContentScale.Crop,
        alpha = 0.15f
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewJuegoContent() {
    JuegoContent(
        uiState = JuegoUiState(
            nombreJugador = "Carlos", saldoActual = 120, rachaActual = 2,
            tipoApuesta = "pleno", valorApuesta = "17", cantidadApuesta = "10",
            resultadoRuleta = 17, ganancia = 350, bonusRacha = 0,
            mensajeResultado = "Salió el 17. Has ganado 350 monedas."
        ),
        onCantidadApuestaChange  = {},
        onSeleccionarPleno       = {},
        onSeleccionarDocena      = {},
        onSeleccionarRojo        = {},
        onSeleccionarNegro       = {},
        onSeleccionarPar         = {},
        onSeleccionarImpar       = {},
        onSeleccionarNumeroPleno = {},
        onSeleccionarDocenaValor = {},
        onGirarClick             = {},
        onGuardarCapturaClick    = {},
        onDescartarCapturaClick  = {},
        onHistorialClick         = {},
        onMenuClick              = {},
        onAjustesClick           = {},
        onSalirClick             = {},
        onVolverClick            = {}
    )
}
