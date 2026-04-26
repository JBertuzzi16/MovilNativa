package com.example.spin36.feature.bienvenida

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spin36.R
import com.example.spin36.feature.components.rememberSoundClick
import com.example.spin36.ui.theme.casinoAntracitaSecundario
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoDoradoDetalles
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde

@Composable
fun BienvenidaScreen(
    viewModel: BienvenidaViewModel = viewModel(),
    onEntrarClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BienvenidaContent(
        nombreActual = uiState.nombre,
        onNombreCambiado = { viewModel.onNombreChange(it) },
        onEntrarClick = {
            if (uiState.nombre.isNotBlank()) {
                onEntrarClick(uiState.nombre)
            }
        }
    )
}

@Composable
fun BienvenidaContent(
    nombreActual: String,
    onNombreCambiado: (String) -> Unit,
    onEntrarClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = casinoVerde)
    ) {
        ImagenRuleta(
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LogoSpin36()

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CampoIngresoNombre(
                    nombreActual = nombreActual,
                    onNombreCambiado = onNombreCambiado
                )

                BotonContinuar(
                    onClick = onEntrarClick
                )
            }
        }
    }
}

@Composable
fun ImagenRuleta(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ruleta),
        contentDescription = "Imagen Ruleta de Fondo",
        modifier = modifier.size(500.dp),
        contentScale = ContentScale.Crop,
        alpha = 0.85f
    )
}

@Composable
fun LogoSpin36() {
    val fuenteRuleta = FontFamily(Font(R.font.mileast, FontWeight.Normal))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "SPIN36",
            color = casinoAntracitaSecundario,
            fontSize = 50.sp,
            fontFamily = fuenteRuleta,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.logo_spin36),
            contentDescription = "Logo SPIN36",
            modifier = Modifier
                .width(500.dp)
        )
    }
}

@Composable
fun CampoIngresoNombre(
    nombreActual: String,
    onNombreCambiado: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(18.dp),
                shadow = Shadow(
                    radius = 20.dp,
                    color = casinoDoradoDetalles.copy(alpha = 0.35f),
                    offset = DpOffset(0.dp, 16.dp)
                )
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(casinoDoradoDetalles, casinoBlanco)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(2.dp)
    ) {
        OutlinedTextField(
            value = nombreActual,
            onValueChange = { if (it.length <= 15) onNombreCambiado(it) },
            placeholder = { Text("Ingresa tu nombre...", color = casinoAntracitaSecundario) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = casinoAntracitaSecundario,
                unfocusedTextColor = casinoAntracitaSecundario,
                focusedContainerColor = casinoBlanco,
                unfocusedContainerColor = casinoBlanco,
                focusedBorderColor = casinoDoradoDetalles,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

}

@Composable
fun BotonContinuar(onClick: () -> Unit) {
    val fuenteRuleta = FontFamily(Font(R.font.mileast, FontWeight.Normal))
    val onClickSonoro = rememberSoundClick(onClick)

    Box(
        modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(18.dp),
                shadow = Shadow(
                    radius = 20.dp,
                    color = casinoDoradoDetalles.copy(alpha = 0.35f),
                    offset = DpOffset(0.dp, 16.dp)
                )
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(casinoDoradoDetalles, casinoRojoAcciones)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(2.dp)
    ) {
        Button(
            onClick = onClickSonoro,
            colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "CONTINUAR",
                color = casinoBlanco,
                fontSize = 25.sp,
                fontFamily = fuenteRuleta,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Preview(showBackground = true, showSystemUi = true, name = "Pantalla Bienvenida Completa")
@Composable
fun PreviewBienvenidaCompleta() {
    BienvenidaContent(
        nombreActual = "KOLDO",
        onNombreCambiado = {},
        onEntrarClick = {}
    )
}