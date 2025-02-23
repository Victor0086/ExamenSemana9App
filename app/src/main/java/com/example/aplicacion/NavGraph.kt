package com.example.aplicacion

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onCreateAccountClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("recover") },
                onLoginSuccess = { isTemporary ->
                    if (isTemporary) {
                        navController.navigate("changePassword")
                    } else {
                        navController.navigate("menu")
                    }
                }
            )
        }

        composable("menu") {
            MenuScreen(
                onNavigateToEscribirYHablar = { navController.navigate("escribirYHablar") },
                onNavigateToProductos = { navController.navigate("products") },
                onNavigateToAyuda = { navController.navigate("ayuda") }
            )
        }

        composable("escribirYHablar") {
            EscribirYHablarScreen(onBack = { navController.popBackStack() })
        }

        composable("products") {
            ProductScreen(
                onBack = { navController.popBackStack() },
                onLogoutClick = { navController.navigate("login") },
                onNavigateToHablar = { navController.navigate("escribirYHablar") }
            )
        }

        composable("ayuda") {
            AyudaScreen(onBack = { navController.popBackStack() })
        }
    }
}

