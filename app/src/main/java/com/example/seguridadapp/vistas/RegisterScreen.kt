package com.example.seguridadapp.vistas

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
            // Título principal
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Campo de correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Correo electrónico") },
                placeholder = { Text("Nombre de la red") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mín. 6)") },
                placeholder = { Text("Contraseña") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPass) "Ocultar" else "Mostrar"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Campo de confirmación de contraseña
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("Repite contraseña") },
                placeholder = { Text("Contraseña") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showConfirm) "Ocultar" else "Mostrar"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Botón de registro
            Button(
                onClick = {
                    validarRegistro(email, password, confirm, auth, context) {
                        navController.popBackStack()
                        navController.navigate("login")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Registrar")
            }

            Spacer(Modifier.height(16.dp))

            // Enlace para volver al login
            TextButton(onClick = {
                navController.popBackStack()
                navController.navigate("login")
            }) {
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
    onSucess: () -> Unit
) {
    when {
        email.isBlank() || password.isBlank() || confirm.isBlank() ->
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()

        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            Toast.makeText(context, "Correo inválido", Toast.LENGTH_SHORT).show()

        password.length < 6 ->
            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()

        password != confirm ->
            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()

        else -> {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        onSucess()
                    } else {
                        Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}





