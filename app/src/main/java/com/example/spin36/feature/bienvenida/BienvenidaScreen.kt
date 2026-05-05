package com.example.spin36.feature.bienvenida

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.example.spin36.R
import com.example.spin36.feature.components.rememberSoundClick
import com.example.spin36.ui.theme.casinoAntracitaSecundario
import com.example.spin36.ui.theme.casinoBlanco
import com.example.spin36.ui.theme.casinoDoradoDetalles
import com.example.spin36.ui.theme.casinoRojoAcciones
import com.example.spin36.ui.theme.casinoVerde

@Composable
fun BienvenidaScreen(
    viewModel: BienvenidaViewModel = viewModel(),
    onEntrarClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            viewModel.procesarResultadoGoogle(account.idToken) { nombre ->
                onEntrarClick(nombre)
            }
        } catch (e: ApiException) {
            viewModel.procesarExcepcionGoogle(e)
        }
    }

    BienvenidaContent(
        uiState        = uiState,
        onGoogleClick  = {
            val client = viewModel.obtenerGoogleSignInClient(context)
            googleLauncher.launch(client.signInIntent)
        }
    )
}

@Composable
fun BienvenidaContent(
    uiState: BienvenidaUiState,
    onGoogleClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(color = casinoVerde)) {
        ImagenRuleta(modifier = Modifier.align(Alignment.BottomCenter))
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LogoSpin36()
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.error != null) {
                    Text(
                        text       = uiState.error,
                        color      = casinoRojoAcciones,
                        fontFamily = FontFamily(Font(R.font.mileast, FontWeight.Normal)),
                        fontSize   = 15.sp
                    )
                }
                if (uiState.cargando) {
                    Row(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = casinoBlanco)
                    }
                } else {
                    BotonGoogle(onClick = onGoogleClick)
                }
            }
        }
    }
}

@Composable
fun ImagenRuleta(modifier: Modifier = Modifier) {
    Image(
        painter            = painterResource(id = R.drawable.ruleta),
        contentDescription = null,
        modifier           = modifier.size(500.dp),
        contentScale       = ContentScale.Crop,
        alpha              = 0.85f
    )
}

@Composable
fun LogoSpin36() {
    val fuenteRuleta = FontFamily(Font(R.font.mileast, FontWeight.Normal))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "SPIN36", color = casinoAntracitaSecundario, fontSize = 50.sp, fontFamily = fuenteRuleta, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Image(painter = painterResource(id = R.drawable.logo_spin36), contentDescription = "Logo SPIN36", modifier = Modifier.width(500.dp))
    }
}

@Composable
fun BotonGoogle(onClick: () -> Unit) {
    val fuenteRuleta  = FontFamily(Font(R.font.mileast, FontWeight.Normal))
    val onClickSonoro = rememberSoundClick(onClick)
    Box(
        modifier = Modifier
            .dropShadow(shape = RoundedCornerShape(18.dp), shadow = Shadow(radius = 20.dp, color = casinoDoradoDetalles.copy(alpha = 0.35f), offset = DpOffset(0.dp, 16.dp)))
            .background(brush = Brush.linearGradient(colors = listOf(casinoDoradoDetalles, casinoRojoAcciones)), shape = RoundedCornerShape(16.dp))
            .padding(2.dp)
    ) {
        Button(
            onClick  = onClickSonoro,
            colors   = ButtonDefaults.buttonColors(containerColor = casinoRojoAcciones),
            shape    = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Image(
                painter            = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                modifier           = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text       = stringResource(R.string.bienvenida_google),
                color      = casinoBlanco,
                fontSize   = 20.sp,
                fontFamily = fuenteRuleta,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBienvenidaCompleta() {
    BienvenidaContent(
        uiState       = BienvenidaUiState(),
        onGoogleClick = {}
    )
}
