import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
import com.example.aplicacion.R


@Composable
fun HablarScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var textToSpeak by remember { mutableStateOf("Hola") }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    // Inicializar TTS
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
            } else {
                Log.e("HablarScreen", "Error al inicializar TextToSpeech")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)), // 🔹 Fondo azul claro como en otras pantallas
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Logo de la app en la parte superior
            Image(
                painter = painterResource(id = R.drawable.panda),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            )

            // 🔹 Título de la pantalla
            Text(
                text = "Texto a hablar:",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 🔹 Campo de texto para ingresar el mensaje
            BasicTextField(
                value = textToSpeak,
                onValueChange = { textToSpeak = it },
                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color(0xFFF5D2E3)) // Mismo color rosa claro que antes
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Botón para reproducir el texto en voz
            Button(
                onClick = {
                    tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E4A5A)), // Mismo color de "Reproducir"
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Reproducir", color = Color.White)
            }

            // 🔹 Botón para volver a la pantalla anterior
            Button(
                onClick = { onBack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray), // Botón de volver en gris
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Volver", color = Color.White)
            }
        }
    }
}
