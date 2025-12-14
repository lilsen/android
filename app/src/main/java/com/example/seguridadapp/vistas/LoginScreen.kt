package com.example.seguridadapp.vistas

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
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
            // Título principal
            Text(
                text = "Theremin Security",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                placeholder = { Text("ejemplo@correo.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Enlaces
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { /* Lógica recuperar */ }) {
                    Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.bodySmall)
                }
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de conexión
            Button(
                onClick = {
                    validarCredencial(email, password, auth, context, navController)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("INGRESAR")
            }
        }
    }
}

private fun validarCredencial(
    email: String,
    password: String,
    auth: FirebaseAuth,
    context: Context,
    navController: NavController
) {
    if (email.isNotBlank() && password.isNotBlank()) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()

                // --- CAMBIO IMPORTANTE AQUÍ ---
                // No vamos al home directo. Vamos a validar la conexión física.
                navController.navigate("conectar") {
                    // Borramos el login del historial para que no vuelva atrás
                    popUpTo("login") { inclusive = true }
                }

            } else {
                Toast.makeText(context, "Error: Verifica tus datos", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        Toast.makeText(context, "Por favor completa los campos", Toast.LENGTH_SHORT).show()
    }
}