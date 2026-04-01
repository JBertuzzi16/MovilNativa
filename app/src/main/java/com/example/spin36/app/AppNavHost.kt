package com.example.spin36.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spin36.feature.bienvenida.BienvenidaScreen
import com.example.spin36.feature.juego.JuegoScreen
import com.example.spin36.feature.juego.JuegoViewModel

@Composable
fun AppNavHost(juegoViewModel: JuegoViewModel) {
    // Este es el "GPS" de tu aplicación
    val navController = rememberNavController()

    // Configuramos el mapa de la app. Empezamos en "bienvenida"
    NavHost(navController = navController, startDestination = "bienvenida") {

        // --- RUTA 1: PANTALLA DE BIENVENIDA ---
        composable("bienvenida") {
            BienvenidaScreen(
                onEntrarClick = { nombreJugador ->
                    // Cuando el usuario presiona CONTINUAR, navegamos al juego
                    // y enviamos el nombre como si fuera una ruta de internet
                    navController.navigate("juego/$nombreJugador")
                }
            )
        }

        // --- RUTA 2: PANTALLA DEL JUEGO ---
        composable(
            route = "juego/{nombreJugador}", // Esperamos recibir el parámetro
            arguments = listOf(navArgument("nombreJugador") { type = NavType.StringType })
        ) { backStackEntry ->
            // Rescatamos el nombre que viene en la ruta de navegación
            val nombreRecibido = backStackEntry.arguments?.getString("nombreJugador") ?: "INVITADO"

            LaunchedEffect(nombreRecibido) {
                juegoViewModel.cargarJugador(nombreRecibido)
            }

            JuegoScreen(viewModel = juegoViewModel)
        }
    }
}