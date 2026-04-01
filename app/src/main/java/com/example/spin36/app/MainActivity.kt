package com.example.spin36.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.spin36.ui.theme.Spin36Theme
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.spin36.data.database.Spin36Database
import com.example.spin36.data.repository.CasinoRepository
import com.example.spin36.feature.juego.JuegoScreen
import com.example.spin36.feature.juego.JuegoViewModel
import com.example.spin36.feature.juego.JuegoViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Spin36Database.getDatabase(applicationContext)
        val dao = database.CasinoDAO()
        val repository = CasinoRepository(dao)
        val factory = JuegoViewModelFactory(repository)

        val viewModel = ViewModelProvider(this, factory)[JuegoViewModel::class.java]

        setContent {
            AppNavHost(juegoViewModel = viewModel)
        }
    }
}