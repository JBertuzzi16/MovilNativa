package com.example.iskracode.feature.bienvenida

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BienvenidaScreen (){
    Box(Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Login(Modifier.align(Alignment.Center))
    }
}

@Composable
fun Login(modifier: Modifier) {
        Column(modifier = modifier){
            Cabezera()
        }
}

@Composable
fun Cabezera() {

}
