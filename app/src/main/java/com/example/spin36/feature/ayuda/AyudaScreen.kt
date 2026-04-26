package com.example.spin36.feature.ayuda

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.spin36.feature.components.PantallaActual
import com.example.spin36.feature.components.Spin36TopBar
import com.example.spin36.ui.theme.casinoVerde

@Composable
fun AyudaScreen(
    onVolverClick: () -> Unit,
    onMenuClick: () -> Unit,
    onJuegoClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onAjustesClick: () -> Unit,
    onSalirClick: () -> Unit
) {
    Scaffold(
        containerColor = casinoVerde,
        topBar = {
            Spin36TopBar(
                titulo            = "AYUDA",
                pantallaActual    = PantallaActual.AYUDA,
                onIrMenu          = onMenuClick,
                onIrJuego         = onJuegoClick,
                onIrHistorial     = onHistorialClick,
                onIrAjustes       = onAjustesClick,
                onSalirConfirmado = onSalirClick
            )
        }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = false
                    loadUrl("file:///android_asset/ayuda.html")
                }
            }
        )
    }
}
