package com.example.spin36.feature.ayuda

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.spin36.R
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
    val configuration = LocalConfiguration.current
    val idioma = configuration.locales[0].language
    val urlHtml = when (idioma) {
        "en" -> "file:///android_asset/ayuda_en.html"
        "ca" -> "file:///android_asset/ayuda_ca.html"
        "eu" -> "file:///android_asset/ayuda_eu.html"
        else -> "file:///android_asset/ayuda.html"
    }

    Scaffold(
        containerColor = casinoVerde,
        topBar = {
            Spin36TopBar(
                titulo            = stringResource(R.string.ayuda_titulo),
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
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            factory  = { ctx ->
                WebView(ctx).apply {
                    webViewClient              = WebViewClient()
                    settings.javaScriptEnabled = false
                    loadUrl(urlHtml)
                }
            }
        )
    }
}
