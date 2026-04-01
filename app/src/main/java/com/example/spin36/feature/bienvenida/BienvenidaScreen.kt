package com.example.spin36.feature.bienvenida

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spin36.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun BienvenidaScreen(
    viewModel: BienvenidaViewModel = viewModel(),
    onEntrarClick : (String)-> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val casinoVerde = Color(0xFF0F5C3A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(casinoVerde)
    ){
        // Capa 1: Fondo
        ImagenRuleta(
            modifier = Modifier.align(Alignment.Center)
        )

        // Capa 2: Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Arriba: Logo
            LogoSpin36()

            // Abajo: Input y Botón agrupados
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CampoIngresoNombre(
                    nombreActual = uiState.nombre,
                    onNombreCambiado = { viewModel.onNombreChange(it) }
                )

                BotonContinuar(
                    onClick = {
                        if (uiState.nombre.isNotBlank()){
                            onEntrarClick(uiState.nombre)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ImagenRuleta(modifier: Modifier= Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ruleta),
        contentDescription = "Imagen Ruleta de Fondo",
        modifier = modifier.fillMaxWidth(),
        contentScale = ContentScale.Fit,
        alpha = 0.8f
    )
}

@Composable
fun LogoSpin36() {
    val fuenteRuleta = FontFamily(Font(R.font.mileast, FontWeight.Normal))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "SPIN36",
            color = Color(0xFA000000),
            fontSize = 42.sp,
            fontFamily = fuenteRuleta,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.logo_spin36),
            contentDescription = "Logo SPIN36",
            modifier = Modifier.width(140.dp).height(80.dp)
        )
    }
}

@Composable
fun CampoIngresoNombre(
    nombreActual: String,
    onNombreCambiado: (String)-> Unit
) {
    OutlinedTextField(
        value = nombreActual,
        onValueChange = { if (it.length <= 15) onNombreCambiado(it) },
        placeholder = { Text ("Ingresa tu nombre...") },
        singleLine = true,
        shape = RoundedCornerShape(25.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFFFFD700),
            unfocusedBorderColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BotonContinuar(onClick: () -> Unit) {
    val fuenteRuleta = FontFamily(Font(R.font.mileast, FontWeight.Normal))
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(
            text = "CONTINUAR",
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = fuenteRuleta,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Pantalla Bienvenida Completa")
@Composable
fun PreviewBienvenidaCompleta() {
    BienvenidaScreen(
        onEntrarClick = { /* Solo diseño */ }
    )
}