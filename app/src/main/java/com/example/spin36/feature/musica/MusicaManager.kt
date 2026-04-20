package com.example.spin36.feature.musica

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import com.example.spin36.R

class MusicaManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var enPausa: Boolean = false

    //volumen predefinido
    private var volumenActual: Float = 0.7f

    //volumen durante notificaciones
    private val VOLUMEN_DUCKING = 0.2f

    //recuerda si la musicaestaba sonando para reanudarla o no
    private var sonandoAntesDePerdida: Boolean = false


    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    //listener, libera audio si el dispositivo lo necesita
    private val focusListener = AudioManager.OnAudioFocusChangeListener { cambio ->
        when (cambio) {

            //cierre permanente del audio si lo soliciita para no reanudarlo luego
            AudioManager.AUDIOFOCUS_LOSS -> {
                sonandoAntesDePerdida = false
                pausarInterno()
            }

            //cierre transitorio, por llamadas o similar,luego reanuda
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                sonandoAntesDePerdida = mediaPlayer?.isPlaying == true
                pausarInterno()
            }

            //cierre transitorio por nitificación baja volumen
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                sonandoAntesDePerdida = mediaPlayer?.isPlaying == true
                mediaPlayer?.setVolume(VOLUMEN_DUCKING, VOLUMEN_DUCKING)
            }

            //recupera el foco al terminar llamada o similar
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Siempre restaura el volumen normal
                mediaPlayer?.setVolume(volumenActual, volumenActual)
                // Solo reanuda si la música estaba sonando antes de la interrupción
                if (sonandoAntesDePerdida) {
                    reanudarInterno()
                    sonandoAntesDePerdida = false
                }
            }
        }
    }


    //audiofocus
    private val focusRequest: AudioFocusRequest? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                //para conceder el foco
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(focusListener)
                .build()
        } else null

    fun reproducirOficial() {
        if (!solicitarFoco()) return
        detenerReproductor()
        mediaPlayer = MediaPlayer.create(context, R.raw.musica_oficial).apply {
            isLooping = true
            setVolume(volumenActual, volumenActual)
            start()
        }
        enPausa = false
    }

    //carga y reproduce una cancion del dispositivo a partir del URI
    fun reproducirUri(uri: Uri) {
        if (!solicitarFoco()) return
        detenerReproductor()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, uri)
            isLooping = true
            prepare()
            setVolume(volumenActual, volumenActual)
            start()
        }
        enPausa = false
    }

    //pausa manual
    fun pausar() {
        sonandoAntesDePerdida = false
        pausarInterno()
    }

    //reanuda despues de pausa manual
    fun reanudar() {
        if (enPausa) {
            if (solicitarFoco()) {
                reanudarInterno()
            }
        }
    }

    //para la musica librea el mediaplayer i deja el foco de audio
    fun detener() {
        sonandoAntesDePerdida = false
        detenerReproductor()
        abandonarFoco()
    }

    //cmbia volumen en tiempo real
    fun setVolumen(valor: Float) {
        volumenActual = valor.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(volumenActual, volumenActual)
    }

    fun getVolumen(): Float = volumenActual

    fun estaReproduciendo(): Boolean = mediaPlayer?.isPlaying == true


    fun onAppEnPrimer(): Unit = reanudar()
    fun onAppEnBackground(): Unit = pausar()


    //pausa interna usada por foco y listener
    private fun pausarInterno() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            enPausa = true
        }
    }

    //reanuda interna usada por foco y reaanudar
    private fun reanudarInterno() {
        if (enPausa) {
            mediaPlayer?.start()
            enPausa = false
        }
    }

    //libera mediaplayersin tocarel foco
    private fun detenerReproductor() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        enPausa = false
    }

    //solicita el foco
    private fun solicitarFoco(): Boolean {
        val resultado = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(focusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        return resultado == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ||
               resultado == AudioManager.AUDIOFOCUS_REQUEST_DELAYED
    }

    //liberarfoco
    private fun abandonarFoco() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusListener)
        }
    }
}
