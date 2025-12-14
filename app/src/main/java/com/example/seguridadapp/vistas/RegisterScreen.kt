package com.example.seguridadapp.vistas

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var showPass by rememberSaveable { mutableStateOf(false) }
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Campo Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mín. 6)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Campo Confirmar
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("Repite contraseña") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Botón Registro
            Button(
                onClick = {
                    validarRegistro(email, password, confirm, auth, context) {
                        // --- CAMBIO IMPORTANTE AQUÍ ---
                        // Al registrarse con éxito, vamos a Conectar (ya estamos logueados)
                        navController.navigate("conectar") {
                            popUpTo("register") { inclusive = true }
                            popUpTo("login") { inclusive = true } // Limpiamos todo hacia atrás
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("REGISTRARME")
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

private fun validarRegistro(
    email: String,
    password: String,
    confirm: String,
    auth: FirebaseAuth,
    context: Context,
    onSuccess: () -> Unit
) {
    when {
        email.isBlank() || password.isBlank() || confirm.isBlank() ->
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()

        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            Toast.makeText(context, "Correo inválido", Toast.LENGTH_SHORT).show()

        password.length < 6 ->
            Toast.makeText(context, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()

        password != confirm ->
            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()

        else -> {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } else {
                        Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}