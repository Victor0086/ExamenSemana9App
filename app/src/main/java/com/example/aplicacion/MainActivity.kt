package com.example.aplicacion

import androidx.compose.ui.tooling.preview.Preview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacion.ui.theme.AplicacionTheme
import kotlinx.coroutines.launch
import kotlin.random.Random
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import java.util.Locale


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

// Array para almacenar usuarios registrados y sus contraseñas temporales
val usuariosRegistrados = mutableStateListOf<Pair<String, String>>()
val contraseñasTemporales = mutableSetOf<String>()

@Composable
fun MainContent() {
    var currentScreen by remember { mutableStateOf("login") }
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de pantalla
        Image(
            painter = painterResource(id = R.drawable.fondo_azul),
            contentDescription = "Fondo de pantalla azul",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Pantalla de Login, Registro, Recuperación y Productos
        when (currentScreen) {
            "login" -> {
                LoginScreen(
                    onCreateAccountClick = { currentScreen = "register" },
                    onForgotPasswordClick = { currentScreen = "recover" },
                    onLoginSuccess = { isTemporary ->
                        currentScreen = if (isTemporary) "changePassword" else "products"
                    }
                )
            }
            "register" -> {
                RegisterScreen(
                    onBackToLoginClick = { currentScreen = "login" }
                )
            }
            "recover" -> {
                RecoverPasswordScreen(
                    onBackToLoginClick = { currentScreen = "login" }
                )
            }
            "changePassword" -> {
                ChangePasswordScreen(
                    onPasswordChanged = { currentScreen = "login" }
                )
            }
            "products" -> {
                ProductScreen(
                    onLogoutClick = { currentScreen = "login" }
                )
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun ProductScreen(onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Productos Disponibles",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Producto 1
                ProductItem(
                    name = "Cachupin",
                    description = "Alimento Perro Adulto Cachupin Carne y Arroz 15kg",
                    imageRes = R.drawable.alimento_perro_adulto_cachupin_carne_y_arroz_15kg
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Producto 2
                ProductItem(
                    name = "Cannes",
                    description = "Alimento Perro Adulto Cannes Carne y Cereales 15g",
                    imageRes = R.drawable.alimento_perro_adulto_cannes_carne_y_cereales_15g
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Producto 3
                ProductItem(
                    name = "Doko",
                    description = "Alimento Perro Adulto Doko Carne y Pollo 15 kg",
                    imageRes = R.drawable.alimento_perro_adulto_doko_carne_y_pollo_15_kg
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Button(onClick = onLogoutClick) {
                    Text(text = "Cerrar sesión")
                }
            }
        }
    }
}


@Composable
fun ProductItem(name: String, description: String, imageRes: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Imagen de $name",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = TextStyle(fontSize = 16.sp)
        )
    }
}


@Composable
fun LoginScreen(
    onCreateAccountClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: (Boolean) -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val loginError = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope() // Para manejar las coroutines

    // Estado para control de accesibilidad
    var isHighContrast by remember { mutableStateOf(false) }
    var textSize by remember { mutableStateOf(14.sp) }

    // Función para alternar el modo de alto contraste
    fun toggleHighContrast() {
        isHighContrast = !isHighContrast
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isHighContrast) Color.Black else Color(0xFFE3F2FD)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(if (isHighContrast) Color.White else Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
                .width(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Peet Food",
                        style = TextStyle(
                            color = if (isHighContrast) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = textSize
                        ),
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.panda),
                        contentDescription = "Logo de Peet Food, El Panda",
                        modifier = Modifier
                            .size(160.dp)
                            .padding(vertical = 4.dp)
                    )
                    Text(
                        text = "El Panda",
                        style = TextStyle(
                            color = if (isHighContrast) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = textSize
                        ),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                TextField(
                    shape = RoundedCornerShape(20.dp),
                    value = email.value,
                    onValueChange = { email.value = it },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text(text = "Correo Electrónico", fontSize = textSize) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(20.dp)),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                TextField(
                    shape = RoundedCornerShape(20.dp),
                    value = password.value,
                    onValueChange = { password.value = it },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text(text = "Contraseña", fontSize = textSize) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(20.dp)),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )

                if (loginError.value) {
                    Text(
                        text = "Correo o contraseña incorrectos",
                        color = Color.Red,
                        fontSize = textSize,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        val userIndex = usuariosRegistrados.indexOfFirst { it.first == email.value }
                        if (userIndex != -1 && usuariosRegistrados[userIndex].second == password.value) {
                            val isTemporary = contraseñasTemporales.contains(password.value)
                            onLoginSuccess(isTemporary)
                            loginError.value = false


                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Inicio de sesión exitoso")
                            }
                        } else {
                            loginError.value = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
                ) {
                    Text(text = "Iniciar sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = textSize)
                }

                TextButton(
                    onClick = {
                        onCreateAccountClick()
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(text = "Crear Cuenta", color = Color(0xFF0D47A1), fontSize = textSize)
                }

                TextButton(onClick = {
                    onForgotPasswordClick()
                }) {
                    Text(text = "¿Olvidaste tu contraseña?", color = Color(0xFF0D47A1), fontSize = textSize)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de accesibilidad
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { toggleHighContrast() }) {
                        Text(
                            text = if (isHighContrast) "Modo normal" else "Modo alto contraste",
                            fontSize = textSize
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .align(Alignment.BottomCenter) // Alineación horizontal centrada en la parte inferior
        )
    }
}










@Composable
fun ChangePasswordScreen(onPasswordChanged: () -> Unit) {
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val error = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Cambio de contraseña exitoso") },
            text = { Text(text = "Tu contraseña ha sido cambiada correctamente.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    onPasswordChanged()
                }) {
                    Text(text = "Aceptar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
            .width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cambiar Contraseña",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            shape = RoundedCornerShape(12.dp),
            value = newPassword.value,
            onValueChange = { newPassword.value = it },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Nueva Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(12.dp))
        )

        TextField(
            shape = RoundedCornerShape(12.dp),
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(12.dp))
        )

        if (error.value) {
            Text(
                text = "Las contraseñas no coinciden",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                if (newPassword.value == confirmPassword.value && newPassword.value.isNotEmpty()) {
                    val userIndex = usuariosRegistrados.indexOfFirst {
                        it.second == contraseñasTemporales.firstOrNull { temp -> temp == it.second }
                    }

                    if (userIndex != -1) {
                        usuariosRegistrados[userIndex] = usuariosRegistrados[userIndex].first to newPassword.value
                        contraseñasTemporales.remove(usuariosRegistrados[userIndex].second)
                        showDialog.value = true
                    }
                } else {
                    error.value = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
        ) {
            Text(text = "Cambiar contraseña", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecoverPasswordScreen(onBackToLoginClick: () -> Unit) {
    val email = remember { mutableStateOf("") }
    val recoverError = remember { mutableStateOf(false) }
    val recoverSuccess = remember { mutableStateOf(false) }
    var recoveredPassword by remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Contraseña Recuperada") },
            text = {
                Column {
                    Text(text = "Tu contraseña temporal es:")
                    Text(
                        text = recoveredPassword,
                        color = Color.Blue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text(text = "Aceptar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
            .width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recuperar Contraseña",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            shape = RoundedCornerShape(20.dp),
            value = email.value,
            onValueChange = { email.value = it },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(20.dp))
        )

        if (recoverError.value) {
            Text(
                text = "Correo no registrado.",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                val userIndex = usuariosRegistrados.indexOfFirst { it.first == email.value }
                if (userIndex != -1) {
                    recoveredPassword = generateTemporaryPassword()
                    usuariosRegistrados[userIndex] = email.value to recoveredPassword
                    contraseñasTemporales.add(recoveredPassword)
                    recoverSuccess.value = true
                    recoverError.value = false
                    showDialog.value = true
                } else {
                    recoverError.value = true
                    recoverSuccess.value = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
        ) {
            Text(text = "Recuperar contraseña", color = Color.White, fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = onBackToLoginClick) {
            Text(text = "Volver a Iniciar sesión", color = Color(0xFF0D47A1))
        }
    }
}

fun generateTemporaryPassword(): String {
    val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..8)
        .map { chars.random() }
        .joinToString("")
}

@Composable
fun RegisterScreen(onBackToLoginClick: () -> Unit) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val registrationError = remember { mutableStateOf(false) }
    val registrationSuccess = remember { mutableStateOf(false) }
    val userExistsError = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Registro exitoso") },
            text = { Text(text = "Cuenta creada con éxito.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    onBackToLoginClick()
                }) {
                    Text(text = "Aceptar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
            .width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            shape = RoundedCornerShape(12.dp),
            value = email.value,
            onValueChange = { email.value = it },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(12.dp))
        )

        TextField(
            shape = RoundedCornerShape(12.dp),
            value = password.value,
            onValueChange = { password.value = it },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(12.dp))
        )

        TextField(
            shape = RoundedCornerShape(12.dp),
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Repetir Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(12.dp))
        )

        if (registrationError.value) {
            Text(
                text = "Error en el registro. Revisa los campos.",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (userExistsError.value) {
            Text(
                text = "El usuario ya está registrado.",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                if (usuariosRegistrados.any { it.first == email.value }) {
                    userExistsError.value = true
                    registrationError.value = false
                    registrationSuccess.value = false
                } else if (email.value.isNotEmpty() &&
                    password.value.isNotEmpty() &&
                    password.value == confirmPassword.value &&
                    usuariosRegistrados.size < 5
                ) {
                    usuariosRegistrados.add(email.value to password.value)
                    registrationSuccess.value = true
                    registrationError.value = false
                    userExistsError.value = false
                    showDialog.value = true
                } else {
                    registrationError.value = true
                    registrationSuccess.value = false
                    userExistsError.value = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
        ) {
            Text(text = "Crear cuenta", color = Color.White, fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = onBackToLoginClick) {
            Text(text = "Volver a Iniciar sesión", color = Color(0xFF0D47A1))
        }
    }
}


