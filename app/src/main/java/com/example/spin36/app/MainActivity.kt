package com.example.spin36.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.spin36.data.database.Spin36Database
import com.example.spin36.data.repository.CasinoRepository
import com.example.spin36.feature.historial.HistorialViewModel
import com.example.spin36.feature.historial.HistorialViewModelFactory
import com.example.spin36.feature.juego.JuegoViewModel
import com.example.spin36.feature.juego.JuegoViewModelFactory
import com.example.spin36.ui.theme.Spin36Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Spin36Database.getDatabase(applicationContext)
        val dao = database.CasinoDAO()
        val repository = CasinoRepository(dao)

        val juegoFactory = JuegoViewModelFactory(repository)
        val historialFactory = HistorialViewModelFactory(repository)

        val juegoViewModel =
            ViewModelProvider(this, juegoFactory)[JuegoViewModel::class.java]

        val historialViewModel =
            ViewModelProvider(this, historialFactory)[HistorialViewModel::class.java]

        setContent {
            Spin36Theme {
                AppNavHost(
                    juegoViewModel = juegoViewModel,
                    historialViewModel = historialViewModel
                )
            }
        }
    }
}