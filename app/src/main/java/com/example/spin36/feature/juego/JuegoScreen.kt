package com.example.spin36.feature.juego


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuegoScreen(
    viewModel: JuegoViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargarJugador()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spin36") }
            )
        }
    ) { padding ->
        JuegoContent(
            uiState = uiState,
            onTipoApuestaChange = viewModel::onTipoApuestaChange,
            onValorApuestaChange = viewModel::onValorApuestaChange,
            onCantidadApuestaChange = viewModel::onCantidadApuestaChange,
            onJugarClick = viewModel::jugar,
            padding = padding
        )
    }
}

@Composable
fun JuegoContent(
    uiState: JuegoUiState,
    onTipoApuestaChange: (String) -> Unit,
    onValorApuestaChange: (String) -> Unit,
    onCantidadApuestaChange: (String) -> Unit,
    onJugarClick: () -> Unit,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Jugador: ${uiState.nombreJugador}",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Saldo actual: ${uiState.saldoActual}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Racha actual: ${uiState.rachaActual}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.tipoApuesta,
            onValueChange = onTipoApuestaChange,
            label = { Text("Tipo de apuesta") },
            placeholder = { Text("pleno / color / docena / par_impar") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.valorApuesta,
            onValueChange = onValorApuestaChange,
            label = { Text("Valor de apuesta") },
            placeholder = { Text("7 / rojo / 2 / par") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.cantidadApuesta,
            onValueChange = onCantidadApuestaChange,
            label = { Text("Cantidad apostada") },
            placeholder = { Text("10") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

      //temporal
        Text("cargando: ${uiState.cargando}")
        Text("juegoTerminado: ${uiState.juegoTerminado}")
        Button(
            onClick = onJugarClick,
            modifier = Modifier.fillMaxWidth(),
            //enabled = !uiState.cargando && !uiState.juegoTerminado
        ) {
            Text("Jugar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.resultadoRuleta != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Número ganador: ${uiState.resultadoRuleta}")
                    Text("Monedas ganadas: ${uiState.monedasGanadas}")
                    Text("Bonus racha: ${uiState.bonusRacha}")
                    Text("Mensaje: ${uiState.mensajeResultado}")
                }
            }
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (uiState.juegoTerminado) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Te has quedado sin saldo. Fin de la sesión.",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}