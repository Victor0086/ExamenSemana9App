package com.example.aplicacion

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasTestTag
import org.junit.Rule
import org.junit.Test

class LoginScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginScreen() {
        // Lanzar la pantalla de login
        composeTestRule.setContent {
            LoginScreen(
                onCreateAccountClick = {},
                onForgotPasswordClick = {},
                onLoginSuccess = {}
            )
        }

        // Interactuar con los elementos
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("123456")
        composeTestRule.onNodeWithTag("loginButton").performClick()
    }
}
