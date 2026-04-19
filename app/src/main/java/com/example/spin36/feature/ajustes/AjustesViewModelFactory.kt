package com.example.spin36.feature.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spin36.data.preferences.AjustesPreferences
import com.example.spin36.feature.musica.MusicaManager
//instanciamos el viewmodewl
class AjustesViewModelFactory(
    private val prefs: AjustesPreferences,
    private val musicaManager: MusicaManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AjustesViewModel::class.java)) {
            return AjustesViewModel(prefs, musicaManager) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
