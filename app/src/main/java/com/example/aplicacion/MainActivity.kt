package com.example.aplicacion

import HablarScreen
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import java.util.Locale
import androidx.compose.foundation.layout.*


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.testTag
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

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

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_azul),
            contentDescription = "Fondo de pantalla azul",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
            "register" -> RegisterScreen(onBackToLoginClick = { currentScreen = "login" })
            "recover" -> RecoverPasswordScreen(onBackToLoginClick = { currentScreen = "login" })
            "changePassword" -> ChangePasswordScreen(onPasswordChanged = { currentScreen = "login" })
            "products" -> ProductScreen(
                onLogoutClick = { currentScreen = "login" },
                onNavigateToHablar = { currentScreen = "hablar" }
            )
            "hablar" -> HablarScreen(onBack = { currentScreen = "products" })

        }
    }
}


@Composable
fun ProductScreen(onLogoutClick: () -> Unit, onNavigateToHablar: () -> Unit) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo y Nombre de la tienda
            Image(
                painter = painterResource(id = R.drawable.panda),
                contentDescription = "Logo Panda",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "Peet Food El Panda",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Lista de productos con LazyColumn que NO oculta el botón
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) //
            ) {
                items(getProductList()) { product ->
                    ProductItem(product) {
                        selectedProduct = product
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Botón de "Texto a Voz"
            Button(
                onClick = onNavigateToHablar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
            ) {
                Text(text = "Texto a Voz", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Botón de "Cerrar Sesión"
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
            ) {
                Text(text = "Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Mostrar detalles del producto seleccionado
    selectedProduct?.let {
        ProductDetailModal(product = it, onDismiss = { selectedProduct = null })
    }
}




@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = "Imagen de ${product.name}",
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = product.name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${product.price}",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun ProductDetailModal(product: Product, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = product.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = "Imagen de ${product.name}",
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = product.description)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Precio: $${product.price}", fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Volver")
            }
        },
        dismissButton = {
            Button(onClick = { /* Lógica para agregar al carro */ }) {
                Text("Agregar al carrito")
            }
        }
    )
}

data class Product(val name: String, val description: String, val price: Double, val imageRes: Int)

fun getProductList() = listOf(
    Product("Cachupin", "Alimento Perro Adulto Cachupin Carne y Arroz 15kg", 19.99, R.drawable.alimento_perro_adulto_cachupin_carne_y_arroz_15kg),
    Product("Cannes", "Alimento Perro Adulto Cannes Carne y Cereales 15kg", 14.99, R.drawable.alimento_perro_adulto_cannes_carne_y_cereales_15g),
    Product("Doko", "Alimento Perro Adulto Doko Carne y Pollo 15kg", 23.49, R.drawable.alimento_perro_adulto_doko_carne_y_pollo_15_kg)
)



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
    // Estados para controlar el foco en los TextFields
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

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
                    // Lottie animation
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pandaanimacion))
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(200.dp)
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
                        focusedContainerColor = if (isEmailFocused) Color(0xFFE3F2FD) else Color(0xFFE0EFFA),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text(text = "Correo Electrónico", fontSize = textSize) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(20.dp))
                        .testTag("emailField"), // Agregado testTag
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                TextField(
                    shape = RoundedCornerShape(20.dp),
                    value = password.value,
                    onValueChange = { password.value = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = if (isPasswordFocused) Color(0xFFE1F0FC) else Color(0xFFE0EFFA),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text(text = "Contraseña", fontSize = textSize) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(20.dp))
                        .testTag("passwordField"), // Agregado testTag
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
                        FirebaseAuthHelper.loginUser(email.value, password.value,
                            onSuccess = { onLoginSuccess(false) },
                            onFailure = { loginError.value = true }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("loginButton"), // Agregado testTag
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
                ) {
                    Text(text = "Iniciar sesión", color = Color.White, fontWeight = FontWeight.Bold)
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
                .align(Alignment.BottomCenter)
        )
    }
}




@Composable
fun ChangePasswordScreen(onPasswordChanged: () -> Unit) {
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val error = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val isNewPasswordFocused by remember { mutableStateOf(false) }
    val isConfirmPasswordFocused by remember { mutableStateOf(false) }


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
            shape = RoundedCornerShape(20.dp),
            value = newPassword.value,
            onValueChange = { newPassword.value = it },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isNewPasswordFocused) Color(0xFFE1F0FC) else Color(0xFFE0EFFA),
                unfocusedContainerColor = Color(0xFFF5F5F5),
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
            shape = RoundedCornerShape(20.dp),
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isConfirmPasswordFocused) Color(0xFFE1F0FC) else Color(0xFFE0EFFA),
                unfocusedContainerColor = Color(0xFFF5F5F5),
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
    val isRestorePasswordFocused by remember { mutableStateOf(false) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(24.dp),
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
                    focusedContainerColor = if (isRestorePasswordFocused) Color(0xFFE1F0FC) else Color(0xFFE0EFFA),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
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
}

// Función para generar una contraseña temporal
fun generateTemporaryPassword(): String {
    val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..8) // Genera una contraseña de 8 caracteres
        .map { chars.random() }
        .joinToString("")
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBackToLoginClick: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val rut = remember { mutableStateOf("") }
    val nombreCompleto = remember { mutableStateOf("") }
    val direccion = remember { mutableStateOf("") }
    val selectedRegion = remember { mutableStateOf("") }
    val selectedComuna = remember { mutableStateOf("") }
    val registrationError = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }

    var expandedRegion by remember { mutableStateOf(false) }
    var expandedComuna by remember { mutableStateOf(false) }

    val regiones = listOf("Metropolitana", "Valparaíso", "Biobío", "Araucanía", "Los Lagos")
    val comunasPorRegion = mapOf(
        "Metropolitana" to listOf("Santiago", "Puente Alto", "Maipú"),
        "Valparaíso" to listOf("Viña del Mar", "Valparaíso", "Quilpué"),
        "Biobío" to listOf("Concepción", "Talcahuano", "Coronel"),
        "Araucanía" to listOf("Temuco", "Villarrica", "Angol"),
        "Los Lagos" to listOf("Puerto Montt", "Osorno", "Castro")
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onBackToLoginClick() // Llevar al inicio de sesión
                }) {
                    Text("Aceptar")
                }
            },
            title = { Text("Registro Exitoso") },
            text = { Text("El usuario ha sido registrado correctamente.") }
        )
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
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            val textFieldModifier = Modifier
                .fillMaxWidth()
                .height(50.dp)

            CustomTextField(value = rut, label = "RUT", modifier = textFieldModifier)
            Spacer(modifier = Modifier.height(2.dp))

            CustomTextField(value = nombreCompleto, label = "Nombre Completo", modifier = textFieldModifier)
            Spacer(modifier = Modifier.height(2.dp))

            CustomTextField(value = direccion, label = "Dirección", modifier = textFieldModifier)
            Spacer(modifier = Modifier.height(2.dp))

            // Selector de Región
            CustomDropdownField(
                label = "Región",
                selectedValue = selectedRegion,
                expanded = expandedRegion,
                onExpandedChange = { expandedRegion = it },
                items = regiones,
                onItemSelected = {
                    selectedRegion.value = it
                    selectedComuna.value = "" // Reiniciar comuna al cambiar región
                    expandedRegion = false
                }
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Selector de Comuna
            CustomDropdownField(
                label = "Comuna",
                selectedValue = selectedComuna,
                expanded = expandedComuna,
                onExpandedChange = { expandedComuna = it },
                items = comunasPorRegion[selectedRegion.value] ?: emptyList(),
                onItemSelected = {
                    selectedComuna.value = it
                    expandedComuna = false
                }
            )
            Spacer(modifier = Modifier.height(2.dp))

            CustomTextField(value = email, label = "Correo Electrónico", modifier = textFieldModifier)
            Spacer(modifier = Modifier.height(2.dp))

            CustomPasswordField(value = password, label = "Contraseña", modifier = textFieldModifier)
            Spacer(modifier = Modifier.height(2.dp))

            CustomPasswordField(value = confirmPassword, label = "Confirmar Contraseña", modifier = textFieldModifier)
            Spacer(modifier = Modifier.height(2.dp))

            // Mostrar mensaje de error si hay problemas
            if (registrationError.value) {
                Text(
                    text = errorMessage.value,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Botón de Crear Cuenta con Firebase Authentication y Firestore
            Button(
                onClick = {
                    if (rut.value.isBlank() || nombreCompleto.value.isBlank() || direccion.value.isBlank() ||
                        email.value.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank() ||
                        selectedRegion.value.isBlank() || selectedComuna.value.isBlank()
                    ) {
                        registrationError.value = true
                        errorMessage.value = "Todos los campos son obligatorios"
                        return@Button
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                        registrationError.value = true
                        errorMessage.value = "Correo electrónico no válido"
                        return@Button
                    }

                    if (password.value.length < 6) {
                        registrationError.value = true
                        errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
                        return@Button
                    }

                    if (password.value != confirmPassword.value) {
                        registrationError.value = true
                        errorMessage.value = "Las contraseñas no coinciden"
                        return@Button
                    }

                    auth.createUserWithEmailAndPassword(email.value, password.value)
                        .addOnCompleteListener { task: Task<AuthResult> ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid

                                if (userId != null) {
                                    val usuario = hashMapOf(
                                        "userId" to userId,
                                        "rut" to rut.value,
                                        "nombre_completo" to nombreCompleto.value,
                                        "region" to selectedRegion.value,
                                        "direccion" to direccion.value,
                                        "comuna" to selectedComuna.value,
                                        "email" to email.value
                                    )

                                    firestore.collection("UsuariosRegistrados")
                                        .document(userId)  // Guardar con el mismo UID de Firebase Auth
                                        .set(usuario)
                                        .addOnSuccessListener {
                                            showDialog.value = true
                                            registrationError.value = false
                                        }
                                        .addOnFailureListener {
                                            registrationError.value = true
                                            errorMessage.value = "Error al registrar usuario en Firestore"
                                        }
                                } else {
                                    registrationError.value = true
                                    errorMessage.value = "Error al obtener el UID del usuario"
                                }
                            } else {
                                registrationError.value = true
                                errorMessage.value = task.exception?.message ?: "Error en el registro"
                            }
                        }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
            ) {
                Text(text = "Crear cuenta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            TextButton(onClick = onBackToLoginClick) {
                Text(text = "Volver a Iniciar sesión", color = Color(0xFF0D47A1), fontSize = 16.sp)
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownField(
    label: String,
    selectedValue: MutableState<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        TextField(
            value = selectedValue.value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 16.sp, color = Color.Black) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE3F2FD),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 8.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .menuAnchor()
                .clickable { onExpandedChange(true) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, fontSize = 16.sp, color = Color.Black) },
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}



@Composable
fun CustomTextField(value: MutableState<String>, label: String, modifier: Modifier = Modifier) {
    TextField(
        value = value.value,
        onValueChange = { value.value = it },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE3F2FD),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        placeholder = { Text(label, fontSize = 16.sp, color = Color.Black) },
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 8.dp)
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
    )
}

@Composable
fun CustomPasswordField(value: MutableState<String>, label: String, modifier: Modifier = Modifier) {
    TextField(
        value = value.value,
        onValueChange = { value.value = it },
        label = { Text(label, fontSize = 16.sp, color = Color.Black) },
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE3F2FD),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
    )
}

