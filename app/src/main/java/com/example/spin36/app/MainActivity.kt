package com.example.spin36.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.spin36.data.database.Spin36Database
import com.example.spin36.data.preferences.AjustesPreferences
import com.example.spin36.data.repository.CasinoRepository
import com.example.spin36.feature.ajustes.AjustesViewModel
import com.example.spin36.feature.ajustes.AjustesViewModelFactory
import com.example.spin36.feature.galeria.GaleriaManager
import com.example.spin36.feature.historial.HistorialViewModel
import com.example.spin36.feature.historial.HistorialViewModelFactory
import com.example.spin36.feature.juego.JuegoViewModel
import com.example.spin36.feature.juego.JuegoViewModelFactory
import com.example.spin36.feature.musica.MusicaManager
import com.example.spin36.ui.theme.Spin36Theme

class MainActivity : ComponentActivity() {

    private lateinit var musicaManager: MusicaManager
    private lateinit var ajustesPreferences: AjustesPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //bbdd y repos
        val database   = Spin36Database.getDatabase(applicationContext)
        val dao        = database.CasinoDAO()
        val repository = CasinoRepository(dao)

        //musica
        ajustesPreferences = AjustesPreferences(applicationContext)
        musicaManager      = MusicaManager(applicationContext)
        arrancarMusicaSegunPreferencias()

        //viewmodels
        val juegoViewModel = ViewModelProvider(
            this, JuegoViewModelFactory(repository, GaleriaManager(applicationContext))
        )[JuegoViewModel::class.java]

        val historialViewModel = ViewModelProvider(
            this, HistorialViewModelFactory(repository)
        )[HistorialViewModel::class.java]

        val ajustesViewModel = ViewModelProvider(
            this, AjustesViewModelFactory(ajustesPreferences, musicaManager)
        )[AjustesViewModel::class.java]

        setContent {
            Spin36Theme {
                AppNavHost(
                    juegoViewModel     = juegoViewModel,
                    historialViewModel = historialViewModel,
                    ajustesViewModel   = ajustesViewModel
                )
            }
        }
    }

    //pausa musica al ir al background
    override fun onStop() {
        super.onStop()
        if (ajustesPreferences.musicaActivada) {
            musicaManager.onAppEnBackground()
        }
    }

    //reanuda la musica al tener en primer plano
    override fun onStart() {
        super.onStart()
        if (ajustesPreferences.musicaActivada) {
            musicaManager.onAppEnPrimer()
        }
    }

    //librea mediaplayer al cerrar la app
    override fun onDestroy() {
        musicaManager.detener()
        super.onDestroy()
    }

    //inicia musica segun persistencia
    private fun arrancarMusicaSegunPreferencias() {
        if (!ajustesPreferences.musicaActivada) return

        //volumen guardado
        musicaManager.setVolumen(ajustesPreferences.volumenMusica)

        val uriGuardada = ajustesPreferences.uriMusicaPersonalizada
        if (uriGuardada != null) {
            try {
                musicaManager.reproducirUri(Uri.parse(uriGuardada))
            } catch (e: Exception) {
                //si la URI ya no contiene la cancion se pone musica oficiaql
                ajustesPreferences.restablecerMusicaOficial()
                musicaManager.reproducirOficial()
            }
        } else {
            musicaManager.reproducirOficial()
        }
    }
}
