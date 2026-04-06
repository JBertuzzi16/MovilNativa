package com.example.spin36.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spin36.feature.bienvenida.BienvenidaScreen
import com.example.spin36.feature.historial.HistorialScreen
import com.example.spin36.feature.historial.HistorialViewModel
import com.example.spin36.feature.juego.JuegoScreen
import com.example.spin36.feature.juego.JuegoViewModel
import com.example.spin36.feature.menu.MenuScreen

@Composable
fun AppNavHost(
    juegoViewModel: JuegoViewModel,
    historialViewModel: HistorialViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "bienvenida"
    ) {

        composable("bienvenida") {
            BienvenidaScreen(
                onEntrarClick = { nombreJugador ->
                    navController.navigate("menu/$nombreJugador")
                }
            )
        }

        composable(
            route = "menu/{nombreJugador}",
            arguments = listOf(
                navArgument("nombreJugador") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val nombreRecibido =
                backStackEntry.arguments?.getString("nombreJugador") ?: "INVITADO"

            MenuScreen(
                nombreJugador = nombreRecibido,
                onApostarClick = {
                    navController.navigate("juego/$nombreRecibido")
                },
                onHistorialClick = {
                    navController.navigate("historial/$nombreRecibido")
                },
                onSalirClick = {
                    navController.popBackStack("bienvenida", inclusive = false)
                },
                onVolverClick = {
                    navController.popBackStack("bienvenida", inclusive = false)
                }
            )
        }

        composable(
            route = "juego/{nombreJugador}",
            arguments = listOf(
                navArgument("nombreJugador") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val nombreRecibido =
                backStackEntry.arguments?.getString("nombreJugador") ?: "INVITADO"

            LaunchedEffect(nombreRecibido) {
                juegoViewModel.cargarJugador(nombreRecibido)
            }

            JuegoScreen(
                viewModel = juegoViewModel,
                onHistorialClick = {
                    navController.navigate("historial/$nombreRecibido")
                },
                onMenuClick = {
                    val existeEnBackStack = navController.popBackStack(
                        "menu/$nombreRecibido",
                        inclusive = false
                    )

                    if (!existeEnBackStack) {
                        navController.navigate("menu/$nombreRecibido") {
                            launchSingleTop = true
                        }
                    }
                },
                onSalirClick = {
                    juegoViewModel.cerrarSesionActual {
                        navController.popBackStack("bienvenida", inclusive = false)
                    }
                },
                onVolverClick = {
                    val volvio = navController.popBackStack("bienvenida", inclusive = false)

                    if (!volvio) {
                        navController.navigate("bienvenida") {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(
            route = "historial/{nombreJugador}",
            arguments = listOf(
                navArgument("nombreJugador") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val nombreRecibido =
                backStackEntry.arguments?.getString("nombreJugador") ?: "INVITADO"

            HistorialScreen(
                viewModel = historialViewModel,
                onSalirClick = {
                    navController.popBackStack("bienvenida", inclusive = false)
                },
                onIrMenuClick = {
                    navController.navigate("menu/$nombreRecibido") {
                        launchSingleTop = true
                    }
                },
                onIrJuegoClick = {
                    navController.navigate("juego/$nombreRecibido") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}