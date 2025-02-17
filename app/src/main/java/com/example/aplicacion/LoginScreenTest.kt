package com.example.aplicacion

import org.junit.Test
import org.junit.Assert.*

class LoginScreenTest {

    @Test
    fun testLoginValidation() {
        val email = "test@email.com"
        val password = "123456"
        val expected = true

        val result = validateLogin(email, password)
        assertEquals(expected, result)
    }

    // Simulación de función de validación de login
    fun validateLogin(email: String, password: String): Boolean {
        return email == "test@email.com" && password == "123456"
    }
}
