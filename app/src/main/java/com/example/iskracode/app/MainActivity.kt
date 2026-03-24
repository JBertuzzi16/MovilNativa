package com.example.iskracode.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.iskracode.feature.bienvenida.BienvenidaScreen
import com.example.iskracode.ui.theme.IskraCodeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IskraCodeTheme {
                AppNavHost()
            }
            BienvenidaScreen()
        }
    }
}