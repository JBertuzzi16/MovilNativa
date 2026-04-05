package com.example.spin36.feature.menu

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spin36.R
import com.example.spin36.feature.historial.LogoSpin36
import com.example.spin36.feature.historial.fuenteRuleta
import com.example.spin36.ui.theme.casinoAntracitaSecundario
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde
import org.intellij.lang.annotations.JdkConstants

@Composable
fun MenuScreen(
    nombreJugador: String,
    onApostarClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(casinoVerde)
    ) {
        ImagenRuleta(
            modifier = Modifier.align(Alignment.Center)
        )
        ImagenTapete(
            modifier = Modifier.align(Alignment.Center)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = nombreJugador,
                    color = casinoBlanco,
                    fontSize = 26.sp,
                    fontFamily = fuenteRuletaMenu
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BotonMenu(
                    texto = "APOSTAR",
                    onClick = onApostarClick,
                )

                BotonMenu(
                    texto = "HISTORIAL",
                    onClick = onHistorialClick
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LogoSpin36(
                        modifier = Modifier.fillMaxWidth(0.18f)
                    )

                    OutlinedButton(
                        onClick = onVolverClick,
                        colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
                        shape = RoundedCornerShape(10.dp)
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
            }
        }

    }
}


private val fuenteRuletaMenu = FontFamily(
    Font(R.font.mileast, FontWeight.Normal)
)

@Composable
fun ImagenTapete(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.tapete),
        contentDescription = "Tapete de fondo",
        modifier = modifier.fillMaxWidth(),
        contentScale = ContentScale.Fit,
        alpha = 1f
    )
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
@Composable
fun BotonMenu(
    texto: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = texto,
            color = casinoBlanco,
            fontSize = 22.sp,
            fontFamily = fuenteRuletaMenu,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMenuScreen() {
    MenuScreen(
        nombreJugador = "KOLDO",
        onApostarClick = {},
        onHistorialClick = {},
        onVolverClick = {}
    )
}