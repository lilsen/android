package com.example.seguridadapp.vistas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ConectarScreen(navController: NavController, auth: FirebaseAuth) {
    val redes = listOf(
        Pair("Línea3", "Ocupado"),
        Pair("ESP32", "Disponible"),
        Pair("Primaria", "Disponible"),
        Pair("Red Vecino", "Red Inestable")
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Redes disponibles",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Lista de redes
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                redes.forEach { (nombre, estado) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray)
                            .clickable {
                                if (nombre == "ESP32") {
                                    val email = auth.currentUser?.email
                                    if (email != null) {
                                        navController.popBackStack()
                                        navController.navigate("home/$email?connected=true")
                                    } else {
                                        Toast.makeText(
                                            navController.context,
                                            "No hay sesión activa",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nombre,
                            color = Color.Cyan,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = estado,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
