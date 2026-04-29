package com.example.spin36.feature.components

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.spin36.R

object ButtonSoundPool {
    private var pool: SoundPool? = null
    private var soundId: Int = 0

    fun init(context: Context) {
        if (pool != null) return
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        pool = SoundPool.Builder().setMaxStreams(4).setAudioAttributes(attrs).build()
        soundId = pool!!.load(context.applicationContext, R.raw.button_click, 1)
    }

    fun play() {
        if (soundId != 0) pool?.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        pool?.release()
        pool = null
        soundId = 0
    }
}

@Composable
fun rememberSoundClick(onClick: () -> Unit): () -> Unit {
    return remember(onClick) {
        { ButtonSoundPool.play(); onClick() }
    }
}