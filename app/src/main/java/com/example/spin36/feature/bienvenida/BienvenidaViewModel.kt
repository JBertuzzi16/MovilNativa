package com.example.spin36.feature.bienvenida

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class BienvenidaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BienvenidaUiState())
    val uiState: StateFlow<BienvenidaUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    fun onNombreChange(nuevoNombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nuevoNombre, error = null)
    }

    fun obtenerGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.example.spin36.R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun procesarResultadoGoogle(idToken: String?, onExito: (String) -> Unit) {
        if (idToken == null) {
            _uiState.value = _uiState.value.copy(cargando = false, error = "Token de Google no válido")
            return
        }
        _uiState.value = _uiState.value.copy(cargando = true, error = null)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val nombre = result.user?.displayName ?: "Jugador"
                _uiState.value = _uiState.value.copy(cargando = false, nombre = nombre)
                onExito(nombre)
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(cargando = false, error = e.message)
            }
    }

    fun procesarExcepcionGoogle(e: ApiException) {
        _uiState.value = _uiState.value.copy(
            cargando = false,
            error    = "Error Google Sign-In: ${e.statusCode}"
        )
    }
}
