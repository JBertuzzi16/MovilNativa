package com.example.spin36.data.preferences

import android.content.Context
import android.content.SharedPreferences

//persistencia de ajustes
class AjustesPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(NOMBRE_PREFS, Context.MODE_PRIVATE)

    //marca si la musica esytá activada, por defecto si
    var musicaActivada: Boolean
        get() = prefs.getBoolean(CLAVE_MUSICA_ACTIVADA, true)
        set(valor) = prefs.edit().putBoolean(CLAVE_MUSICA_ACTIVADA, valor).apply()

    //URI de la cancion del dispositivo si null sigue la oficial
    var uriMusicaPersonalizada: String?
        get() = prefs.getString(CLAVE_URI_MUSICA, null)
        set(valor) = prefs.edit().putString(CLAVE_URI_MUSICA, valor).apply()

    //volumen entre 0-1 0.7 por defectgo
    var volumenMusica: Float
        get() = prefs.getFloat(CLAVE_VOLUMEN, 0.7f)
        set(valor) = prefs.edit().putFloat(CLAVE_VOLUMEN, valor).apply()

    //borrar la URI y poner la oficial
    fun restablecerMusicaOficial() {
        prefs.edit().remove(CLAVE_URI_MUSICA).apply()
    }

    companion object {
        private const val NOMBRE_PREFS          = "spin36_ajustes"
        private const val CLAVE_MUSICA_ACTIVADA = "musica_activada"
        private const val CLAVE_URI_MUSICA      = "uri_musica_personalizada"
        private const val CLAVE_VOLUMEN         = "volumen_musica"
    }
}
