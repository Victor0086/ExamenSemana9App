package com.example.aplicacion

import androidx.compose.ui.tooling.preview.Preview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacion.ui.theme.AplicacionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplicacionTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de pantalla
        Image(
            painter = painterResource(id = R.drawable.fondo_azul),
            contentDescription = "Fondo de pantalla",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Pantalla de Login y Registro
        LoginAndRegisterScreen()
    }
}

@Composable
fun LoginAndRegisterScreen() {
    //para alternar entre vistas de Login y Registro
    val isLoginScreen = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoginScreen.value) {
            LoginScreen(
                onCreateAccountClick = { isLoginScreen.value = false }
            )
        } else {
            RegisterScreen(
                onBackToLoginClick = { isLoginScreen.value = true }
            )
        }
    }
}

@Composable
fun LoginScreen(onCreateAccountClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
            .width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen de usuario
        Image(
            painter = painterResource(id = R.drawable.avatar),
            contentDescription = "User Icon",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )

        // Campo de correo
        val email = remember { mutableStateOf("") }
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            placeholder = { Text(text = "Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Campo de contraseña
        val password = remember { mutableStateOf("") }
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            placeholder = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Botón de inicio
        Button(
            onClick = { /* Acción para iniciar sesión */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
        ) {
            Text(text = "Iniciar sesión", color = Color.White, fontWeight = FontWeight.Bold)
        }

        // Botón para ir a la pantalla de registro
        TextButton(onClick = onCreateAccountClick) {
            Text(text = "Crear Cuenta", color = Color(0xFF0D47A1))
        }
    }
}

@Composable
fun RegisterScreen(onBackToLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
            .width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo de correo
        val email = remember { mutableStateOf("") }
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            placeholder = { Text(text = "Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Campo de contraseña
        val password = remember { mutableStateOf("") }
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            placeholder = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Campo para repetir contraseña
        val confirmPassword = remember { mutableStateOf("") }
        TextField(
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            placeholder = { Text(text = "Repetir Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Botón para crear cuenta
        Button(
            onClick = { /* Acción para crear cuenta */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
        ) {
            Text(text = "Crear cuenta", color = Color.White, fontWeight = FontWeight.Bold)
        }

        // Botón para volver a la pantalla de inicio de sesión
        TextButton(onClick = onBackToLoginClick) {
            Text(text = "Volver a Iniciar sesión", color = Color(0xFF0D47A1))
        }
    }
}

@Preview(showBackground = true, widthDp = 1422, heightDp = 840)
@Composable
fun PreviewLoginAndRegisterScreen() {
    AplicacionTheme {
        MainContent()
    }
}
