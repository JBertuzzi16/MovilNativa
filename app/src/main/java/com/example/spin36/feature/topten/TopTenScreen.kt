package com.example.spin36.feature.topten

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spin36.R
import com.example.spin36.data.remote.PuntuacionDto
import com.example.spin36.feature.components.PantallaActual
import com.example.spin36.feature.components.Spin36TopBar
import com.example.spin36.feature.components.rememberSoundClick
import com.example.spin36.feature.historial.LogoSpin36
import com.example.spin36.feature.menu.ImagenRuleta
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoDoradoDetalles
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde

private val fuenteRuleta = FontFamily(Font(R.font.mileast, FontWeight.Normal))

@Composable
fun TopTenScreen(
    viewModel: TopTenViewModel = viewModel(),
    onVolverClick: () -> Unit,
    onMenuClick: () -> Unit,
    onJuegoClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onAjustesClick: () -> Unit,
    onCerrarSesion: () -> Unit = {},
    onSalirClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargarTopTen() }

    val cargando     = uiState.cargando
    val error        = uiState.error
    val puntuaciones = uiState.puntuaciones

    Scaffold(
        containerColor = casinoVerde,
        topBar = {
            Spin36TopBar(
                titulo            = stringResource(R.string.topten_titulo),
                pantallaActual    = PantallaActual.TOPTEN,
                onIrMenu          = onMenuClick,
                onIrJuego         = onJuegoClick,
                onIrHistorial     = onHistorialClick,
                onIrAjustes       = onAjustesClick,
                onCerrarSesion    = onCerrarSesion,
                onSalirConfirmado = onSalirClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().background(casinoVerde).padding(innerPadding)
        ) {
            ImagenRuleta(modifier = Modifier.align(Alignment.Center))

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    cargando -> {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = casinoBlanco)
                        }
                    }
                    error != null && puntuaciones.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Text(text = error, color = casinoRojoAcciones, fontFamily = fuenteRuleta)
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(puntuaciones) { index, puntuacion ->
                                PuntuacionCard(posicion = index + 1, puntuacion = puntuacion)
                            }
                        }
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
                            Text(text = stringResource(R.string.topten_atras), fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = casinoBlanco)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PuntuacionCard(posicion: Int, puntuacion: PuntuacionDto) {
    val colorPosicion = when (posicion) {
        1    -> Color(0xFFFFD700)
        2    -> Color(0xFFC0C0C0)
        3    -> Color(0xFFCD7F32)
        else -> casinoBlanco
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = casinoBlanco.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "#$posicion", color = colorPosicion, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.size(48.dp))
            Text(text = puntuacion.nombre, color = casinoBlanco, fontFamily = fuenteRuleta, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Text(text = "${puntuacion.puntuacion}", color = casinoDoradoDetalles, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}
