package com.example.spin36.feature.components

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberSoundClick(onClick: () -> Unit): () -> Unit {
    val context = LocalContext.current

    val soundPool = remember {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder().setMaxStreams(4).setAudioAttributes(attrs).build()
    }

    val soundId = remember {
        val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as AudioManager
        audioManager.loadSoundEffects()
        soundPool.load(context, com.example.spin36.R.raw.button_click, 1)
    }

    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    return remember(onClick) {
        {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            onClick()
        }
    }
}
