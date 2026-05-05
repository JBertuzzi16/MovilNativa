package com.example.spin36.feature.ajustes

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spin36.R
import com.example.spin36.feature.components.PantallaActual
import com.example.spin36.feature.components.Spin36TopBar
import com.example.spin36.feature.components.rememberSoundClick
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
    onHistorialClick: () -> Unit,
    onAyudaClick: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.cargarAjustes() }

    val selectorAudio = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
        onCambiarIdioma      = { tag -> AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag)) },
        onCerrarSesion       = onCerrarSesion,
        onVolverClick        = onVolverClick,
        onSalirClick         = onSalirClick,
        onMenuClick          = onMenuClick,
        onJuegoClick         = onJuegoClick,
        onHistorialClick     = onHistorialClick,
        onAyudaClick         = onAyudaClick
    )
}

@Composable
fun AjustesContent(
    uiState: AjustesUiState,
    onMusicaToggle: (Boolean) -> Unit,
    onVolumenChange: (Float) -> Unit,
    onElegirMusica: () -> Unit,
    onRestablecerOficial: () -> Unit,
    onCambiarIdioma: (String) -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onVolverClick: () -> Unit,
    onSalirClick: () -> Unit,
    onMenuClick: () -> Unit,
    onJuegoClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onAyudaClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = casinoVerde,
        topBar = {
            Spin36TopBar(
                titulo            = stringResource(R.string.ajustes_titulo),
                pantallaActual    = PantallaActual.AJUSTES,
                onIrMenu          = onMenuClick,
                onIrJuego         = onJuegoClick,
                onIrHistorial     = onHistorialClick,
                onIrAjustes       = {},
                onIrAyuda         = onAyudaClick,
                onCerrarSesion    = onCerrarSesion,
                onSalirConfirmado = onSalirClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(casinoVerde).padding(innerPadding)) {
            ImagenRuleta(modifier = Modifier.align(Alignment.Center))
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(text = stringResource(R.string.ajustes_musica_titulo), fontFamily = fuenteAjustes, fontSize = 28.sp, color = casinoBlanco)
                HorizontalDivider(thickness = 1.dp, color = casinoBlanco.copy(alpha = 0.3f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.ajustes_musica_fondo), fontFamily = fuenteAjustes, fontSize = 22.sp, color = casinoBlanco)
                    Switch(
                        checked         = uiState.musicaActivada,
                        onCheckedChange = onMusicaToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = casinoBlanco, checkedTrackColor = casinoDoradoDetalles, uncheckedThumbColor = casinoBlanco, uncheckedTrackColor = casinoAntracitaSecundario)
                    )
                }

                val nombrePista = if (uiState.uriMusicaPersonalizada != null)
                    uiState.nombreMusicaPersonalizada ?: stringResource(R.string.ajustes_cancion_personalizada)
                else stringResource(R.string.ajustes_melodia_oficial)

                Text(text = stringResource(R.string.ajustes_pista_activa, nombrePista), fontFamily = fuenteAjustes, fontSize = 16.sp, color = casinoBlanco.copy(alpha = 0.75f))
                HorizontalDivider(thickness = 1.dp, color = casinoBlanco.copy(alpha = 0.3f))

                val porcentaje = (uiState.volumen * 100).roundToInt()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.ajustes_volumen), fontFamily = fuenteAjustes, fontSize = 22.sp, color = if (uiState.musicaActivada) casinoBlanco else casinoBlanco.copy(alpha = 0.4f))
                    Text(text = "$porcentaje%", fontFamily = fuenteAjustes, fontSize = 20.sp, color = if (uiState.musicaActivada) casinoDoradoDetalles else casinoDoradoDetalles.copy(alpha = 0.4f))
                }
                Slider(
                    value = uiState.volumen, onValueChange = onVolumenChange, valueRange = 0f..1f, enabled = uiState.musicaActivada,
                    colors = SliderDefaults.colors(thumbColor = casinoDoradoDetalles, activeTrackColor = casinoDoradoDetalles, inactiveTrackColor = casinoBlanco.copy(alpha = 0.3f), disabledThumbColor = casinoBlanco.copy(alpha = 0.3f), disabledActiveTrackColor = casinoBlanco.copy(alpha = 0.2f), disabledInactiveTrackColor = casinoBlanco.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(thickness = 1.dp, color = casinoBlanco.copy(alpha = 0.3f))

                BotonAjustes(texto = stringResource(R.string.ajustes_elegir_dispositivo), onClick = onElegirMusica)
                if (uiState.uriMusicaPersonalizada != null) {
                    BotonAjustesSecundario(texto = stringResource(R.string.ajustes_usar_oficial), onClick = onRestablecerOficial)
                }

                HorizontalDivider(thickness = 1.dp, color = casinoBlanco.copy(alpha = 0.3f))

                Text(text = stringResource(R.string.ajustes_idioma_titulo), fontFamily = fuenteAjustes, fontSize = 28.sp, color = casinoBlanco)
                SelectorIdioma(onCambiarIdioma = onCambiarIdioma)

                Spacer(modifier = Modifier.weight(1f))

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.End) {
                    Box(modifier = Modifier.dropShadow(shape = RoundedCornerShape(10.dp), shadow = Shadow(radius = 20.dp, color = casinoDoradoDetalles.copy(alpha = 0.35f), offset = DpOffset(0.dp, 5.dp)))) {
                        OutlinedButton(onClick = rememberSoundClick(onVolverClick), colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones), shape = RoundedCornerShape(10.dp)) {
                            Text(text = stringResource(R.string.ajustes_atras), fontFamily = fuenteAjustes, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = casinoBlanco)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorIdioma(onCambiarIdioma: (String) -> Unit) {
    val idiomas = listOf(
        Pair("es", stringResource(R.string.ajustes_idioma_es)),
        Pair("en", stringResource(R.string.ajustes_idioma_en)),
        Pair("ca", stringResource(R.string.ajustes_idioma_ca)),
        Pair("eu", stringResource(R.string.ajustes_idioma_eu))
    )
    var expandido by remember { mutableStateOf(false) }
    var seleccionado by remember { mutableStateOf(idiomas[0]) }

    ExposedDropdownMenuBox(
        expanded         = expandido,
        onExpandedChange = { expandido = it },
        modifier         = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value         = seleccionado.second,
            onValueChange = {},
            readOnly      = true,
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            shape         = RoundedCornerShape(12.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedTextColor        = casinoBlanco,
                unfocusedTextColor      = casinoBlanco,
                focusedContainerColor   = casinoBlanco.copy(alpha = 0.1f),
                unfocusedContainerColor = casinoBlanco.copy(alpha = 0.1f),
                focusedBorderColor      = casinoDoradoDetalles,
                unfocusedBorderColor    = casinoBlanco.copy(alpha = 0.5f),
                focusedTrailingIconColor   = casinoDoradoDetalles,
                unfocusedTrailingIconColor = casinoBlanco
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = fuenteAjustes,
                fontSize   = androidx.compose.ui.unit.TextUnit(18f, androidx.compose.ui.unit.TextUnitType.Sp)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded         = expandido,
            onDismissRequest = { expandido = false }
        ) {
            idiomas.forEach { idioma ->
                DropdownMenuItem(
                    text    = { Text(text = idioma.second, fontFamily = fuenteAjustes) },
                    onClick = rememberSoundClick {
                        seleccionado = idioma
                        expandido    = false
                        onCambiarIdioma(idioma.first)
                    }
                )
            }
        }
    }
}

@Composable
private fun BotonAjustes(texto: String, onClick: () -> Unit) {
    Box(modifier = Modifier.dropShadow(shape = RoundedCornerShape(18.dp), shadow = Shadow(radius = 20.dp, color = casinoDoradoDetalles.copy(alpha = 0.35f), offset = DpOffset(0.dp, 16.dp))).background(brush = Brush.linearGradient(colors = listOf(casinoDoradoDetalles, casinoRojoAcciones)), shape = RoundedCornerShape(16.dp)).padding(2.dp)) {
        Button(onClick = rememberSoundClick(onClick), colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text(text = texto, color = casinoBlanco, fontFamily = fuenteAjustes, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun BotonAjustesSecundario(texto: String, onClick: () -> Unit) {
    OutlinedButton(onClick = rememberSoundClick(onClick), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = casinoBlanco)) {
        Text(text = texto, color = casinoBlanco, fontFamily = fuenteAjustes, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

private fun obtenerNombreArchivo(context: android.content.Context, uri: Uri): String? {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val indice = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            if (indice >= 0) cursor.getString(indice) else null
        }
    } catch (e: Exception) { null }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAjustesContent() {
    AjustesContent(
        uiState = AjustesUiState(musicaActivada = true, uriMusicaPersonalizada = null, nombreMusicaPersonalizada = null, volumen = 0.7f),
        onMusicaToggle = {}, onVolumenChange = {}, onElegirMusica = {}, onRestablecerOficial = {},
        onVolverClick = {}, onSalirClick = {}, onMenuClick = {}, onJuegoClick = {}, onHistorialClick = {}
    )
}
