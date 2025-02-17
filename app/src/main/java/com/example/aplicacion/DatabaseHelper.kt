package com.example.aplicacion


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UsuariosRegistrados.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USUARIOS = "usuarios"
        const val COLUMN_RUT = "rut"
        const val COLUMN_NOMBRE_COMPLETO = "nombre_completo"
        const val COLUMN_REGION = "region"
        const val COLUMN_DIRECCION = "direccion"
        const val COLUMN_COMUNA = "comuna"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsuariosTable = """
            CREATE TABLE $TABLE_USUARIOS (
                $COLUMN_RUT TEXT PRIMARY KEY,
                $COLUMN_NOMBRE_COMPLETO TEXT NOT NULL,
                $COLUMN_REGION TEXT NOT NULL,
                $COLUMN_DIRECCION TEXT NOT NULL,
                $COLUMN_COMUNA TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        db?.execSQL(createUsuariosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    /**
     * Método para registrar un usuario en SQLite y en Firebase Firestore.
     */
    fun registrarUsuario(
        rut: String,
        nombreCompleto: String,
        region: String,
        direccion: String,
        comuna: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_RUT, rut)
            put(COLUMN_NOMBRE_COMPLETO, nombreCompleto)
            put(COLUMN_REGION, region)
            put(COLUMN_DIRECCION, direccion)
            put(COLUMN_COMUNA, comuna)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }

        try {
            val resultado = db.insert(TABLE_USUARIOS, null, values)
            if (resultado == -1L) {
                onFailure("Error al registrar usuario en SQLite")
                return
            }

            Log.i("SQLiteInfo", "Usuario registrado en SQLite")

            // Ahora guardar en Firebase Firestore
            val firestore = FirebaseFirestore.getInstance()
            val usuario = hashMapOf(
                "rut" to rut,
                "nombre_completo" to nombreCompleto,
                "region" to region,
                "direccion" to direccion,
                "comuna" to comuna,
                "email" to email
            )

            firestore.collection("UsuariosRegistrados")
                .add(usuario)
                .addOnSuccessListener {
                    Log.i("FirebaseInfo", "Usuario registrado en Firestore")
                    onSuccess()
                }
                .addOnFailureListener {
                    onFailure("Error al registrar usuario en Firestore")
                }
        } catch (e: Exception) {
            Log.e("SQLiteException", "Error en SQLite: ${e.message}", e)
            onFailure("Error en SQLite: ${e.message}")
        } finally {
            db.close()
        }
    }
}
