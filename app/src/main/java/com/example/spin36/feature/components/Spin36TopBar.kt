package com.example.spin36.feature.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.spin36.R
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde

//identifica la pantalla activa para ocultarla en el desplegable
enum class PantallaActual {
    MENU,
    JUEGO,
    HISTORIAL,
    AJUSTES,
    AYUDA
}

private val fuenteRuletaTopBar = FontFamily(
    Font(R.font.mileast, FontWeight.Normal)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spin36TopBar(
    titulo: String,
    pantallaActual: PantallaActual,
    onIrMenu: () -> Unit,
    onIrJuego: () -> Unit,
    onIrHistorial: () -> Unit,
    onIrAjustes: () -> Unit = {},
    onIrAyuda: () -> Unit = {},
    onSalirConfirmado: () -> Unit
) {
    val menuExpandido = remember { mutableStateOf(false) }
    val mostrarDialogoSalir = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = titulo,
                fontFamily = fuenteRuletaTopBar,
                color = casinoBlanco,
                fontSize = 42.sp
            )
        },
        actions = {
            IconButton(
                onClick = { menuExpandido.value = true }
            ) {
                Text(
                    text = "⋮",
                    color = casinoBlanco,
                    fontFamily = fuenteRuletaTopBar,
                    fontSize = 28.sp
                )
            }

            DropdownMenu(
                expanded = menuExpandido.value,
                onDismissRequest = { menuExpandido.value = false }
            ) {
                if (pantallaActual != PantallaActual.MENU) {
                    DropdownMenuItem(
                        text = { Text(text = "Menú", fontFamily = fuenteRuletaTopBar) },
                        onClick = {
                            menuExpandido.value = false
                            onIrMenu()
                        }
                    )
                }

                if (pantallaActual != PantallaActual.JUEGO) {
                    DropdownMenuItem(
                        text = { Text(text = "Juego / Apuesta", fontFamily = fuenteRuletaTopBar) },
                        onClick = {
                            menuExpandido.value = false
                            onIrJuego()
                        }
                    )
                }

                if (pantallaActual != PantallaActual.HISTORIAL) {
                    DropdownMenuItem(
                        text = { Text(text = "Historial", fontFamily = fuenteRuletaTopBar) },
                        onClick = {
                            menuExpandido.value = false
                            onIrHistorial()
                        }
                    )
                }

                //ajustes solo se muestra si no estamos ya en Ajustes
                if (pantallaActual != PantallaActual.AJUSTES) {
                    DropdownMenuItem(
                        text = { Text(text = "Ajustes", fontFamily = fuenteRuletaTopBar) },
                        onClick = {
                            menuExpandido.value = false
                            onIrAjustes()
                        }
                    )
                }

                if (pantallaActual != PantallaActual.AYUDA) {
                    DropdownMenuItem(
                        text = { Text(text = "Ayuda", fontFamily = fuenteRuletaTopBar) },
                        onClick = {
                            menuExpandido.value = false
                            onIrAyuda()
                        }
                    )
                }

                DropdownMenuItem(
                    text = { Text(text = "Salir", fontFamily = fuenteRuletaTopBar) },
                    onClick = {
                        menuExpandido.value = false
                        mostrarDialogoSalir.value = true
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = casinoVerde,
            titleContentColor = casinoBlanco,
            actionIconContentColor = casinoBlanco
        )
    )

    if (mostrarDialogoSalir.value) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSalir.value = false },
            title = {
                Text(text = "Confirmar salida", fontFamily = fuenteRuletaTopBar)
            },
            text = {
                Text(text = "¿Seguro que quieres salir de la app?", fontFamily = fuenteRuletaTopBar)
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoSalir.value = false
                        onSalirConfirmado()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones)
                ) {
                    Text(text = "Sí", color = casinoBlanco, fontFamily = fuenteRuletaTopBar)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoSalir.value = false }) {
                    Text(text = "No", fontFamily = fuenteRuletaTopBar, color = Color.Black)
                }
            }
        )
    }
}
