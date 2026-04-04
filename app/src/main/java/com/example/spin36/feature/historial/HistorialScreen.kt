package com.example.spin36.feature.historial


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spin36.R
import com.example.spin36.data.database.entities.SesionEntity
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoRojoAcciones


val fuenteRuleta= FontFamily(Font(R.font.mileast, FontWeight.Normal))
@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel,
    onSalirClick: () -> Unit,
    onSesionClick: (SesionEntity) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargarHistorialSesiones()
    }

    HistorialContent(
        uiState = uiState,
        onSalirClick = onSalirClick,
        onSesionClick = onSesionClick
    )
}

@Composable
fun HistorialContent(
    uiState: HistorialUiState,
    onSalirClick: () -> Unit,
    onSesionClick: (SesionEntity) -> Unit = {}

) {
    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = "HISTORIAL",
            fontFamily = fuenteRuleta,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            uiState.cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error,
                        color = casinoRojoAcciones,
                        fontFamily = fuenteRuleta
                    )
                }
            }

            uiState.sesiones.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay sesiones guardadas todavía.",
                        fontFamily = fuenteRuleta,
                        color = casinoRojoAcciones
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.sesiones) { sesion ->
                        SesionCard(
                            sesion = sesion,
                            onClick = { onSesionClick(sesion) }
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onSalirClick,
            colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.5f)

        ) {
            Text(
                text = "Salir",
                fontFamily = fuenteRuleta,
                fontWeight = FontWeight.Bold,
                color = casinoBlanco
            )
        }
    }
}

@Composable
fun SesionCard(
    sesion: SesionEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = sesion.nombreJugador,
                fontFamily = fuenteRuleta
            )

            Text(
                text = "Inicio: ${sesion.fechaHoraInicio}",
                fontFamily = fuenteRuleta
            )

            Text(
                text = "Fin: ${sesion.fechaHoraFin ?: "Sesión en curso"}",
                fontFamily = fuenteRuleta
            )

            Text(
                text = "Saldo final: ${sesion.saldoFinal} monedas",
                fontFamily = fuenteRuleta
            )

            Text(
                text = "Racha máxima: ${sesion.rachaMaxima}",
                fontFamily = fuenteRuleta
            )

            Text(
                text = "Apuestas realizadas: ${sesion.apuestasRealizadas}",
                fontFamily = fuenteRuleta
            )
        }
    }
}