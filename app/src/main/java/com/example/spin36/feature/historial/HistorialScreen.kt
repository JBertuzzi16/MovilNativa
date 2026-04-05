package com.example.spin36.feature.historial


import android.text.BoringLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spin36.R
import com.example.spin36.data.database.entities.SesionEntity
import com.example.spin36.feature.bienvenida.BienvenidaScreen
import com.example.spin36.ui.theme.casinoAntracitaSecundario
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import com.example.spin36.feature.menu.ImagenRuleta


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
    Box(modifier = Modifier
        .fillMaxSize()
        .background(casinoVerde)){
        ImagenRuleta(
            modifier = Modifier.align(Alignment.Center)
        )
    Column(horizontalAlignment = Alignment.CenterHorizontally,

        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)


    ) {
        Text(
            text = "HISTORIAL",
            fontFamily = fuenteRuleta,
            fontSize = 42.sp,
            color = casinoBlanco,
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
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxWidth(0.3f)

        ) {
            Text(
                text = "Atrás",
                fontFamily = fuenteRuleta,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = casinoBlanco
            )
        }
    }
    LogoSpin36(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth(0.18f)
            .padding(start = 20.dp, bottom = 30.dp)
    )
    }


}

@Composable
fun LogoSpin36(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_spin36),
        contentDescription = "Logo SPIN36",
        modifier = modifier
            .width(70.dp)
    )
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
                fontFamily = fuenteRuleta,
                fontWeight = FontWeight.Bold,

            )

            Text(
                text = "Inicio: ${sesion.fechaHoraInicio} h.",
                fontFamily = fuenteRuleta
            )


            Text(
                text = "Saldo final:    ${sesion.saldoFinal} monedas",
                fontFamily = fuenteRuleta,
                color = if (sesion.saldoFinal>0) casinoVerde else casinoRojoAcciones
            )

            Text(
                text = "Racha máxima:   ${sesion.rachaMaxima}",
                fontFamily = fuenteRuleta,

            )

            Text(
                text = "Apuestas realizadas: ${sesion.apuestasRealizadas}",
                fontFamily = fuenteRuleta
            )
        }
    }
}
@Composable
fun ImagenRuleta(modifier: Modifier= Modifier){
    Image(
        painter = painterResource(id=R.drawable.ruleta),
        contentDescription = "Ruleta de fondo",
        modifier = modifier.size(550.dp),
        contentScale = ContentScale.Crop,
        alpha = 0.3f
    )
}
//completo
@Preview(showBackground = true, showSystemUi = true, name = "Historial con datos")
@Composable
fun PreviewHistorialCompleta() {
    val estadoFake = HistorialUiState(
        cargando = false,
        error = null,
        sesiones = listOf(
            SesionEntity(
                nombreJugador = "Carlos",
                fechaHoraInicio = "05/04/2026 18:00",
                fechaHoraFin = "05/04/2026 18:45",
                saldoFinal = 1250,
                rachaMaxima = 4,
                apuestasRealizadas = 12
            ),
            SesionEntity(
                nombreJugador = "Lucía",
                fechaHoraInicio = "04/04/2026 20:10",
                fechaHoraFin = "04/04/2026 21:05",
                saldoFinal = 0,
                rachaMaxima = 3,
                apuestasRealizadas = 9
            ),
            SesionEntity(
                nombreJugador = "Lucía",
                fechaHoraInicio = "04/04/2026 20:10",
                fechaHoraFin = "04/04/2026 21:05",
                saldoFinal = 10,
                rachaMaxima = 3,
                apuestasRealizadas = 9
            ),
            SesionEntity(
                nombreJugador = "Lucía",
                fechaHoraInicio = "04/04/2026 20:10",
                fechaHoraFin = "04/04/2026 21:05",
                saldoFinal = 0,
                rachaMaxima = 3,
                apuestasRealizadas = 9
            )
        )
    )

    HistorialContent(
        uiState = estadoFake,
        onSalirClick = {},
        onSesionClick = {}
    )
}
