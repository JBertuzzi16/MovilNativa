package com.example.spin36.feature.ajustes

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.spin36.data.preferences.AjustesPreferences
import com.example.spin36.feature.musica.MusicaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AjustesViewModel(
    private val prefs: AjustesPreferences,
    private val musicaManager: MusicaManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AjustesUiState())
    val uiState: StateFlow<AjustesUiState> = _uiState.asStateFlow()

    //carga preferencias guardadas
    fun cargarAjustes() {
        _uiState.value = AjustesUiState(
            musicaActivada            = prefs.musicaActivada,
            uriMusicaPersonalizada    = prefs.uriMusicaPersonalizada,
            nombreMusicaPersonalizada = extraerNombreDeUri(prefs.uriMusicaPersonalizada),
            volumen                   = prefs.volumenMusica
        )
        //sincroniza volumen guardado
        musicaManager.setVolumen(prefs.volumenMusica)
    }

    //acxtiva o desactiva musica y lo guarda en persistencia
    fun onMusicaActivadaChange(activada: Boolean) {
        prefs.musicaActivada = activada
        _uiState.value = _uiState.value.copy(musicaActivada = activada)

        if (activada) {
            val uriGuardada = prefs.uriMusicaPersonalizada
            if (uriGuardada != null) {
                musicaManager.reproducirUri(Uri.parse(uriGuardada))
            } else {
                musicaManager.reproducirOficial()
            }
            //aplica volumen guardado
            musicaManager.setVolumen(prefs.volumenMusica)
        } else {
            musicaManager.detener()
        }
    }

    //cambio por slider
    fun onVolumenChange(valor: Float) {
        prefs.volumenMusica = valor
        _uiState.value = _uiState.value.copy(volumen = valor)
        musicaManager.setVolumen(valor)
    }

    //cambio a cancion de dispositivo
    fun onMusicaPersonalizadaElegida(uri: Uri, nombre: String) {
        prefs.uriMusicaPersonalizada = uri.toString()
        _uiState.value = _uiState.value.copy(
            uriMusicaPersonalizada    = uri.toString(),
            nombreMusicaPersonalizada = nombre
        )
        if (prefs.musicaActivada) {
            musicaManager.reproducirUri(uri)
            musicaManager.setVolumen(prefs.volumenMusica)
        }
    }

    //restablecer cancion oficial
    fun onRestablecerMusicaOficial() {
        prefs.restablecerMusicaOficial()
        _uiState.value = _uiState.value.copy(
            uriMusicaPersonalizada    = null,
            nombreMusicaPersonalizada = null
        )
        if (prefs.musicaActivada) {
            musicaManager.reproducirOficial()
            musicaManager.setVolumen(prefs.volumenMusica)
        }
    }

    //extrae el nombre de la cancion por URI
    private fun extraerNombreDeUri(uriString: String?): String? {
        if (uriString == null) return null
        return try {
            Uri.parse(uriString).lastPathSegment
        } catch (e: Exception) {
            null
        }
    }
}
