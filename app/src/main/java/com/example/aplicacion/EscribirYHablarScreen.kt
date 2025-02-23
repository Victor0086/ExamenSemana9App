package com.example.aplicacion


import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore




@Composable
fun EscribirYHablarScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var confirmationMessage by remember { mutableStateOf("") }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var ttsState by remember { mutableStateOf("cargando") }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))
    val progress by animateLottieCompositionAsState(composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true )



    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("es", "ES"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    confirmationMessage = "Idioma no soportado o datos faltantes"
                    ttsState = "error"
                } else {
                    ttsState = "listo"
                }
            } else {
                confirmationMessage = "Error al inicializar TTS"
                ttsState = "error"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Escribe algo:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Escribe aquí...") }
            )

            Spacer(modifier = Modifier.height(8.dp))


            if (ttsState == "cargando" || ttsState == "reproduciendo") {
                LottieAnimation(
                    composition = composition,
                    progress = progress,
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (text.isNotEmpty() && tts != null) {
                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        confirmationMessage = "Texto reproducido"
                        ttsState = "reproduciendo"
                    } else {
                        confirmationMessage = "TTS no está listo"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Reproducir", color = Color.White)
            }

            if (confirmationMessage.isNotEmpty()) {
                Text(text = confirmationMessage, color = Color.Green)
            }

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Volver", color = Color.White)
            }
        }
    }
}

