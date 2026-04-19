package com.example.spin36.feature.musica

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.spin36.R

//control de la reporduccion de msica de fondo
class MusicaManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var enPausa: Boolean = false

    private var volumenActual: Float = 0.7f


    fun reproducirOficial() {
        detener()
        mediaPlayer = MediaPlayer.create(context, R.raw.musica_oficial).apply {
            isLooping = true
            setVolume(volumenActual, volumenActual)
            start()
        }
        enPausa = false
    }

    fun reproducirUri(uri: Uri) {
        detener()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, uri)
            isLooping = true
            prepare()
            setVolume(volumenActual, volumenActual)
            start()
        }
        enPausa = false
    }

    fun pausar() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            enPausa = true
        }
    }

    fun reanudar() {
        if (enPausa) {
            mediaPlayer?.start()
            enPausa = false
        }
    }

    //detener y liberar el reproductor
    fun detener() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        enPausa = false
    }

    //cambia el volumen en tiempo real
    fun setVolumen(valor: Float) {
        volumenActual = valor.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(volumenActual, volumenActual)
    }

    //devuelve volumen actual para la UI
    fun getVolumen(): Float = volumenActual

    //true si el reproductor está sonando
    fun estaReproduciendo(): Boolean = mediaPlayer?.isPlaying == true


    fun onAppEnPrimer(): Unit = reanudar()
    fun onAppEnBackground(): Unit = pausar()
}
