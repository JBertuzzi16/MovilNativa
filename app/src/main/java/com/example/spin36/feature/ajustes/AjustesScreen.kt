package com.example.spin36.feature.ajustes

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spin36.R
import com.example.spin36.feature.components.PantallaActual
import com.example.spin36.feature.components.Spin36TopBar
import com.example.spin36.feature.menu.ImagenRuleta
import com.example.spin36.ui.theme.casinoAntracitaSecundario
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoDoradoDetalles
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde
import kotlin.math.roundToInt

private val fuenteAjustes = FontFamily(Font(R.font.mileast, FontWeight.Normal))

@Composable
fun AjustesScreen(
    viewModel: AjustesViewModel,
    onVolverClick: () -> Unit,
    onSalirClick: () -> Unit,
    onMenuClick: () -> Unit,
    onJuegoClick: () -> Unit,
    onHistorialClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.cargarAjustes()
    }

    val selectorAudio = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val nombre = obtenerNombreArchivo(context, uri) ?: uri.lastPathSegment ?: "Archivo"
            viewModel.onMusicaPersonalizadaElegida(uri, nombre)
        }
    }

    AjustesContent(
        uiState              = uiState,
        onMusicaToggle       = { viewModel.onMusicaActivadaChange(it) },
        onVolumenChange      = { viewModel.onVolumenChange(it) },
        onElegirMusica       = { selectorAudio.launch(arrayOf("audio/*")) },
        onRestablecerOficial = { viewModel.onRestablecerMusicaOficial() },
        onVolverClick        = onVolverClick,
        onSalirClick         = onSalirClick,
        onMenuClick          = onMenuClick,
        onJuegoClick         = onJuegoClick,
        onHistorialClick     = onHistorialClick
    )
}

@Composable
fun AjustesContent(
    uiState: AjustesUiState,
    onMusicaToggle: (Boolean) -> Unit,
    onVolumenChange: (Float) -> Unit,
    onElegirMusica: () -> Unit,
    onRestablecerOficial: () -> Unit,
    onVolverClick: () -> Unit,
    onSalirClick: () -> Unit,
    onMenuClick: () -> Unit,
    onJuegoClick: () -> Unit,
    onHistorialClick: () -> Unit
) {
    Scaffold(
        containerColor = casinoVerde,
        topBar = {
            Spin36TopBar(
                titulo            = "AJUSTES",
                pantallaActual    = PantallaActual.AJUSTES,
                onIrMenu          = onMenuClick,
                onIrJuego         = onJuegoClick,
                onIrHistorial     = onHistorialClick,
                onIrAjustes       = {},
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
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                //titulo
                Text(
                    text       = "MÚSICA",
                    fontFamily = fuenteAjustes,
                    fontSize   = 28.sp,
                    color      = casinoBlanco
                )

                HorizontalDivider(thickness = 1.dp, color = casinoBlanco.copy(alpha = 0.3f))

                //switch on/off
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "Música de fondo",
                        fontFamily = fuenteAjustes,
                        fontSize   = 22.sp,
                        color      = casinoBlanco
                    )
                    Switch(
                        checked         = uiState.musicaActivada,
                        onCheckedChange = onMusicaToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor   = casinoBlanco,
                            checkedTrackColor   = casinoDoradoDetalles,
                            uncheckedThumbColor = casinoBlanco,
                            uncheckedTrackColor = casinoAntracitaSecundario
                        )
                    )
                }

                //cancion actual
                val nombrePista = when {
                    uiState.uriMusicaPersonalizada != null ->
                        uiState.nombreMusicaPersonalizada ?: "Canción personalizada"
                    else -> "Melodía oficial del juego"
                }
                Text(
                    text       = "Pista activa: $nombrePista",
                    fontFamily = fuenteAjustes,
                    fontSize   = 16.sp,
                    color      = casinoBlanco.copy(alpha = 0.75f)
                )

                HorizontalDivider(thickness = 1.dp, color = casinoBlanco.copy(alpha = 0.3f))

                //sliuder de volumen solo cuando la musica está activa
                val porcentaje = (uiState.volumen * 100).roundToInt()

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "Volumen",
                        fontFamily = fuenteAjustes,
                        fontSize   = 22.sp,
                        color      = if (uiState.musicaActivada) casinoBlanco
                                     else casinoBlanco.copy(alpha = 0.4f)
                    )
                    Text(
                        text       = "$porcentaje%",
                        fontFamily = fuenteAjustes,
                        fontSize   = 20.sp,
                        color      = if (uiState.musicaActivada) casinoDoradoDetalles
                                     else casinoDoradoDetalles.copy(alpha = 0.4f)
                    )
                }

                Slider(
                    value          = uiState.volumen,
                    onValueChange  = onVolumenChange,
                    valueRange     = 0f..1f,
                    enabled        = uiState.musicaActivada,
                    colors = SliderDefaults.colors(
                        thumbColor            = casinoDoradoDetalles,
                        activeTrackColor      = casinoDoradoDetalles,
                        inactiveTrackColor    = casinoBlanco.copy(alpha = 0.3f),
                        disabledThumbColor    = casinoBlanco.copy(alpha = 0.3f),
                        disabledActiveTrackColor   = casinoBlanco.copy(alpha = 0.2f),
                        disabledInactiveTrackColor = casinoBlanco.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(thickness = 1.dp, color = casinoBlanco.copy(alpha = 0.3f))

                //botones musica dispositivo
                BotonAjustes(texto = "ELEGIR DEL DISPOSITIVO", onClick = onElegirMusica)

                if (uiState.uriMusicaPersonalizada != null) {
                    BotonAjustesSecundario(
                        texto   = "USAR MELODÍA OFICIAL",
                        onClick = onRestablecerOficial
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                //boton atras
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier.dropShadow(
                            shape  = RoundedCornerShape(10.dp),
                            shadow = Shadow(
                                radius = 20.dp,
                                color  = casinoDoradoDetalles.copy(alpha = 0.35f),
                                offset = DpOffset(0.dp, 5.dp)
                            )
                        )
                    ) {
                        OutlinedButton(
                            onClick = onVolverClick,
                            colors  = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
                            shape   = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text       = "Atrás",
                                fontFamily = fuenteAjustes,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 20.sp,
                                color      = casinoBlanco
                            )
                        }
                    }
                }
            }
        }
    }
}

//botones reutilizables

@Composable
private fun BotonAjustes(texto: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .dropShadow(
                shape  = RoundedCornerShape(18.dp),
                shadow = Shadow(
                    radius = 20.dp,
                    color  = casinoDoradoDetalles.copy(alpha = 0.35f),
                    offset = DpOffset(0.dp, 16.dp)
                )
            )
            .background(
                brush = Brush.linearGradient(colors = listOf(casinoDoradoDetalles, casinoRojoAcciones)),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(2.dp)
    ) {
        Button(
            onClick  = onClick,
            colors   = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
            shape    = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(
                text       = texto,
                color      = casinoBlanco,
                fontFamily = fuenteAjustes,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
        }
    }
}

@Composable
private fun BotonAjustesSecundario(texto: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick  = onClick,
        shape    = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = casinoBlanco)
    ) {
        Text(
            text       = texto,
            color      = casinoBlanco,
            fontFamily = fuenteAjustes,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp
        )
    }
}


private fun obtenerNombreArchivo(context: android.content.Context, uri: Uri): String? {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val indice = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            if (indice >= 0) cursor.getString(indice) else null
        }
    } catch (e: Exception) {
        null
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAjustesContent() {
    AjustesContent(
        uiState = AjustesUiState(
            musicaActivada            = true,
            uriMusicaPersonalizada    = null,
            nombreMusicaPersonalizada = null,
            volumen                   = 0.7f
        ),
        onMusicaToggle       = {},
        onVolumenChange      = {},
        onElegirMusica       = {},
        onRestablecerOficial = {},
        onVolverClick        = {},
        onSalirClick         = {},
        onMenuClick          = {},
        onJuegoClick         = {},
        onHistorialClick     = {}
    )
}
