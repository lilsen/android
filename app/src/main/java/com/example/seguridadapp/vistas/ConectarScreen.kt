package com.example.seguridadapp.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun ConectarScreen(navController: NavController, auth: FirebaseAuth) {
    // 1. ESTADO: IP por defecto (la estática .200 que configuramos en el ESP)
    var ipInput by remember { mutableStateOf("192.168.137.200") }

    // 2. ESTADOS DE UI: Para manejar la carga y mensajes de error
    var connectionStatus by remember { mutableStateOf("Ingrese la IP del dispositivo") }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    // Obtenemos el email para pasarlo al Home (Auditoría ISO)
    val currentUserEmail = auth.currentUser?.email ?: "usuario_anonimo"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Conexión Segura IoT",
                color = Color.Cyan,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "ISO 27001: Autenticación de Dispositivo",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- INPUT DE IP MANUAL ---
            OutlinedTextField(
                value = ipInput,
                onValueChange = { ipInput = it },
                label = { Text("Dirección IP del ESP") },
                singleLine = true,
                // Teclado numérico para evitar errores de dedo
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Cyan
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTÓN INTELIGENTE ---
            Button(
                onClick = {
                    scope.launch {
                        // A. Preparamos la UI (cargando...)
                        isLoading = true
                        isError = false
                        connectionStatus = "Verificando credenciales..."

                        // B. Sanitización (ISO A.14 - Validación de entrada)
                        val cleanIp = ipInput.trim()

                        // C. Handshake de Seguridad
                        // Intentamos saludar al ESP con la API Key
                        val exito = handshakeConElEsp(cleanIp)

                        if (exito) {
                            connectionStatus = "¡Dispositivo Verificado!"

                            // D. NAVEGACIÓN SEGURA
                            // Solo si pasó la prueba, vamos al Home entregando la IP válida
                            navController.navigate("home/$currentUserEmail/$cleanIp") {
                                // Esto evita que al volver atrás regreses a la pantalla de "Cargando"
                                popUpTo("conectar") { inclusive = true }
                            }
                        } else {
                            // Si falla (IP incorrecta o API Key mala)
                            isError = true
                            connectionStatus = "Error: Conexión rechazada. Verifique IP o API Key."
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading, // Bloqueamos botón si ya está cargando
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isError) Color.Red else Color.Cyan,
                    contentColor = Color.Black
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Verificando...")
                } else {
                    Text(if (isError) "REINTENTAR" else "CONECTAR")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Feedback Visual del Estado
            Text(
                text = connectionStatus,
                color = if (isError) Color.Red else Color.Green,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// --- LÓGICA DE VALIDACIÓN (HANDSHAKE) ---
// Esta función intenta hacer una petición real al ESP.
// Si el ESP responde (porque la API Key es correcta), devuelve TRUE.
suspend fun handshakeConElEsp(ip: String): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            // CREDENCIAL DE SEGURIDAD (Debe ser idéntica a la del ESP)
            val apiKey = "PROYECTO_THEREMIN_SECURE"

            // Construimos la URL de prueba
            val url = "http://$ip/data?apiKey=$apiKey"

            // Configuramos un timeout rápido (3 segundos) para no esperar eternamente
            val connection = URL(url).openConnection()
            connection.connectTimeout = 3000
            connection.readTimeout = 3000

            // Intentamos leer la respuesta
            val stream = connection.getInputStream()
            val respuesta = stream.bufferedReader().use { it.readText() }

            // VALIDACIÓN DE INTEGRIDAD
            // Si el JSON contiene la palabra "muestras", es nuestro Theremin.
            respuesta.contains("muestras")

        } catch (e: Exception) {
            e.printStackTrace()
            false // Cualquier error (timeout, 401, 404) retorna falso
        }
    }
}