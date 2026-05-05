package com.example.spin36.feature.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spin36.R
import com.example.spin36.feature.components.PantallaActual
import com.example.spin36.feature.components.Spin36TopBar
import com.example.spin36.feature.components.rememberSoundClick
import com.example.spin36.feature.historial.LogoSpin36
import com.example.spin36.feature.historial.fuenteRuleta
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoDoradoDetalles
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde

private val fuenteRuletaMenu = FontFamily(Font(R.font.mileast, FontWeight.Normal))

@Composable
fun MenuScreen(
    nombreJugador: String,
    onApostarClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onAjustesClick: () -> Unit,
    onAyudaClick: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onSalirClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    Scaffold(
        containerColor = casinoVerde,
        topBar = {
            Spin36TopBar(
                titulo            = stringResource(R.string.menu_titulo),
                pantallaActual    = PantallaActual.MENU,
                onIrMenu          = {},
                onIrJuego         = onApostarClick,
                onIrHistorial     = onHistorialClick,
                onIrAjustes       = onAjustesClick,
                onIrAyuda         = onAyudaClick,
                onCerrarSesion    = onCerrarSesion,
                onSalirConfirmado = onSalirClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(casinoVerde).padding(innerPadding)) {
            ImagenRuleta(modifier = Modifier.align(Alignment.Center))
            ImagenTapete(modifier = Modifier.align(Alignment.Center))
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text(text = nombreJugador, color = casinoBlanco, fontSize = 26.sp, fontFamily = fuenteRuletaMenu)
                }
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    BotonMenu(texto = stringResource(R.string.menu_apostar),    onClick = onApostarClick)
                    BotonMenu(texto = stringResource(R.string.menu_historial),  onClick = onHistorialClick)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        LogoSpin36(modifier = Modifier.fillMaxWidth(0.18f))
                        Box(modifier = Modifier.dropShadow(shape = RoundedCornerShape(10.dp), shadow = Shadow(radius = 20.dp, color = casinoDoradoDetalles.copy(alpha = 0.35f), offset = DpOffset(0.dp, 5.dp)))) {
                            OutlinedButton(onClick = rememberSoundClick(onVolverClick), colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones), shape = RoundedCornerShape(10.dp)) {
                                Text(text = stringResource(R.string.menu_atras), fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = casinoBlanco)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImagenTapete(modifier: Modifier = Modifier) {
    Image(painter = painterResource(id = R.drawable.tapete), contentDescription = null, modifier = modifier.fillMaxWidth(), contentScale = ContentScale.Fit)
}

@Composable
fun ImagenRuleta(modifier: Modifier = Modifier) {
    Image(painter = painterResource(id = R.drawable.ruleta), contentDescription = null, modifier = modifier.size(550.dp), contentScale = ContentScale.Crop, alpha = 0.3f)
}

@Composable
fun BotonMenu(texto: String, onClick: () -> Unit) {
    val onClickSonoro = rememberSoundClick(onClick)
    Box(
        modifier = Modifier
            .dropShadow(shape = RoundedCornerShape(18.dp), shadow = Shadow(radius = 20.dp, color = casinoDoradoDetalles.copy(alpha = 0.35f), offset = DpOffset(0.dp, 16.dp)))
            .background(brush = Brush.linearGradient(colors = listOf(casinoDoradoDetalles, casinoRojoAcciones)), shape = RoundedCornerShape(16.dp))
            .padding(2.dp)
    ) {
        Button(onClick = onClickSonoro, colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text(text = texto, color = casinoBlanco, fontSize = 22.sp, fontFamily = fuenteRuletaMenu, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMenuScreen() {
    MenuScreen(nombreJugador = "KOLDO", onApostarClick = {}, onHistorialClick = {}, onAjustesClick = {}, onSalirClick = {}, onVolverClick = {})
}
