package com.example.spin36.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spin36.feature.ajustes.AjustesScreen
import com.example.spin36.feature.ajustes.AjustesViewModel
import com.example.spin36.feature.ayuda.AyudaScreen
import com.example.spin36.feature.bienvenida.BienvenidaScreen
import com.example.spin36.feature.historial.HistorialScreen
import com.example.spin36.feature.historial.HistorialViewModel
import com.example.spin36.feature.juego.JuegoScreen
import com.example.spin36.feature.juego.JuegoViewModel
import com.example.spin36.feature.menu.MenuScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(
    juegoViewModel: JuegoViewModel,
    historialViewModel: HistorialViewModel,
    ajustesViewModel: AjustesViewModel
) {
    val context       = LocalContext.current
    val navController = rememberNavController()

    fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().build()
        GoogleSignIn.getClient(context, gso).signOut()
        navController.navigate("bienvenida") {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController    = navController,
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
            route     = "menu/{nombreJugador}",
            arguments = listOf(navArgument("nombreJugador") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombreJugador") ?: "INVITADO"

            MenuScreen(
                nombreJugador    = nombre,
                onApostarClick   = { navController.navigate("juego/$nombre") },
                onHistorialClick = { navController.navigate("historial/$nombre") },
                onAjustesClick   = { navController.navigate("ajustes/$nombre") },
                onAyudaClick     = { navController.navigate("ayuda/$nombre") { launchSingleTop = true } },
                onCerrarSesion   = ::cerrarSesion,
                onSalirClick     = { navController.popBackStack("bienvenida", inclusive = false) },
                onVolverClick    = { navController.popBackStack("bienvenida", inclusive = false) }
            )
        }

        composable(
            route     = "juego/{nombreJugador}",
            arguments = listOf(navArgument("nombreJugador") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombreJugador") ?: "INVITADO"

            LaunchedEffect(nombre) {
                juegoViewModel.cargarJugador(nombre)
            }

            JuegoScreen(
                viewModel        = juegoViewModel,
                onHistorialClick = { navController.navigate("historial/$nombre") },
                onMenuClick      = {
                    val existe = navController.popBackStack("menu/$nombre", inclusive = false)
                    if (!existe) navController.navigate("menu/$nombre") { launchSingleTop = true }
                },
                onAjustesClick   = { navController.navigate("ajustes/$nombre") { launchSingleTop = true } },
                onAyudaClick     = { navController.navigate("ayuda/$nombre") { launchSingleTop = true } },
                onCerrarSesion   = ::cerrarSesion,
                onSalirClick     = { juegoViewModel.cerrarSesionActual { navController.popBackStack("bienvenida", inclusive = false) } },
                onVolverClick    = {
                    val volvio = navController.popBackStack("menu/$nombre", inclusive = false)
                    if (!volvio) navController.navigate("menu/$nombre") { launchSingleTop = true }
                }
            )
        }

        composable(
            route     = "historial/{nombreJugador}",
            arguments = listOf(navArgument("nombreJugador") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombreJugador") ?: "INVITADO"

            HistorialScreen(
                viewModel        = historialViewModel,
                onSalirClick     = { navController.popBackStack("bienvenida", inclusive = false) },
                onIrMenuClick    = { navController.navigate("menu/$nombre") { launchSingleTop = true } },
                onIrJuegoClick   = { navController.navigate("juego/$nombre") { launchSingleTop = true } },
                onAjustesClick   = { navController.navigate("ajustes/$nombre") { launchSingleTop = true } },
                onAyudaClick     = { navController.navigate("ayuda/$nombre") { launchSingleTop = true } },
                onCerrarSesion   = ::cerrarSesion,
                onVolverClick    = {
                    val volvio = navController.popBackStack("menu/$nombre", inclusive = false)
                    if (!volvio) navController.navigate("menu/$nombre") { launchSingleTop = true }
                }
            )
        }

        composable(
            route     = "ajustes/{nombreJugador}",
            arguments = listOf(navArgument("nombreJugador") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombreJugador") ?: "INVITADO"

            AjustesScreen(
                viewModel        = ajustesViewModel,
                onVolverClick    = {
                    val volvio = navController.popBackStack("menu/$nombre", inclusive = false)
                    if (!volvio) navController.navigate("menu/$nombre") { launchSingleTop = true }
                },
                onSalirClick     = { navController.popBackStack("bienvenida", inclusive = false) },
                onMenuClick      = { navController.navigate("menu/$nombre") { launchSingleTop = true } },
                onJuegoClick     = { navController.navigate("juego/$nombre") { launchSingleTop = true } },
                onHistorialClick = { navController.navigate("historial/$nombre") { launchSingleTop = true } },
                onAyudaClick     = { navController.navigate("ayuda/$nombre") { launchSingleTop = true } },
                onCerrarSesion   = ::cerrarSesion
            )
        }

        composable(
            route     = "ayuda/{nombreJugador}",
            arguments = listOf(navArgument("nombreJugador") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombreJugador") ?: "INVITADO"

            AyudaScreen(
                onVolverClick    = {
                    val volvio = navController.popBackStack()
                    if (!volvio) navController.navigate("menu/$nombre") { launchSingleTop = true }
                },
                onMenuClick      = {
                    navController.navigate("menu/$nombre") { launchSingleTop = true }
                },
                onJuegoClick     = {
                    navController.navigate("juego/$nombre") { launchSingleTop = true }
                },
                onHistorialClick = {
                    navController.navigate("historial/$nombre") { launchSingleTop = true }
                },
                onAjustesClick   = {
                    navController.navigate("ajustes/$nombre") { launchSingleTop = true }
                },
                onSalirClick     = { navController.popBackStack("bienvenida", inclusive = false) }
            )
        }
    }
}
