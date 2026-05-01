package com.example.spin36.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.spin36.data.database.Spin36Database
import com.example.spin36.data.preferences.AjustesPreferences
import com.example.spin36.data.repository.CasinoRepository
import com.example.spin36.feature.ajustes.AjustesViewModel
import com.example.spin36.feature.ajustes.AjustesViewModelFactory
import com.example.spin36.feature.calendario.CalendarioManager
import com.example.spin36.feature.galeria.GaleriaManager
import com.example.spin36.feature.historial.HistorialViewModel
import com.example.spin36.feature.historial.HistorialViewModelFactory
import com.example.spin36.feature.juego.JuegoViewModel
import com.example.spin36.feature.juego.JuegoViewModelFactory
import com.example.spin36.feature.musica.MusicaManager
import com.example.spin36.feature.notificacion.NotificacionHelper
import com.example.spin36.feature.components.ButtonSoundPool
import com.example.spin36.feature.ubicacion.UbicacionManager
import com.example.spin36.ui.theme.Spin36Theme
import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private lateinit var musicaManager: MusicaManager
    private lateinit var ajustesPreferences: AjustesPreferences

    private val pedirPermisoNotificacion = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ButtonSoundPool.init(applicationContext)

        val database   = Spin36Database.getDatabase(applicationContext)
        val dao        = database.CasinoDAO()
        val repository = CasinoRepository(dao)

        ajustesPreferences = AjustesPreferences(applicationContext)
        musicaManager      = MusicaManager(applicationContext)
        arrancarMusicaSegunPreferencias()

        val juegoViewModel = ViewModelProvider(
            this,
            JuegoViewModelFactory(
                repository         = repository,
                galeriaManager     = GaleriaManager(applicationContext),
                calendarioManager  = CalendarioManager(applicationContext),
                ubicacionManager   = UbicacionManager(applicationContext),
                context            = applicationContext
            )
        )[JuegoViewModel::class.java]

        val historialViewModel = ViewModelProvider(
            this, HistorialViewModelFactory(repository)
        )[HistorialViewModel::class.java]

        val ajustesViewModel = ViewModelProvider(
            this, AjustesViewModelFactory(ajustesPreferences, musicaManager)
        )[AjustesViewModel::class.java]

        // notifiaciones
        NotificacionHelper.crearCanal(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            pedirPermisoNotificacion.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

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

    override fun onStop() {
        super.onStop()
        if (ajustesPreferences.musicaActivada) musicaManager.onAppEnBackground()
    }

    override fun onStart() {
        super.onStart()
        if (ajustesPreferences.musicaActivada) musicaManager.onAppEnPrimer()
    }

    override fun onDestroy() {
        musicaManager.detener()
        ButtonSoundPool.release()
        super.onDestroy()
    }

    private fun arrancarMusicaSegunPreferencias() {
        if (!ajustesPreferences.musicaActivada) return
        musicaManager.setVolumen(ajustesPreferences.volumenMusica)
        val uriGuardada = ajustesPreferences.uriMusicaPersonalizada
        if (uriGuardada != null) {
            try {
                musicaManager.reproducirUri(Uri.parse(uriGuardada))
            } catch (e: Exception) {
                ajustesPreferences.restablecerMusicaOficial()
                musicaManager.reproducirOficial()
            }
        } else {
            musicaManager.reproducirOficial()
        }
    }


}
