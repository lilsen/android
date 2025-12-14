package com.example.seguridadapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seguridadapp.ui.theme.SeguridadAppTheme
import com.example.seguridadapp.vistas.ConectarScreen
import com.example.seguridadapp.vistas.CreditosScreen
import com.example.seguridadapp.vistas.GameScreen
import com.example.seguridadapp.vistas.HomeScreen
import com.example.seguridadapp.vistas.LoginScreen
import com.example.seguridadapp.vistas.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. BLINDAJE PARA PERSISTENCIA (Cumple requisito de almacenamiento offline)
        // Evita crasheos si la actividad se reinicia (rotación de pantalla)
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: Exception) {
            // Si ya estaba activa, continuamos sin problemas
        }

        // 2. INICIALIZACIÓN DE FIREBASE AUTH
        auth = FirebaseAuth.getInstance()

        setContent {
            SeguridadAppTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = "login"
                        ) {
                            // Rutas de Acceso y Configuración
                            composable(route = "login") { LoginScreen(navController, auth) }
                            composable(route = "register") { RegisterScreen(navController, auth) }
                            composable(route = "creditos") { CreditosScreen(navController, auth) }
                            composable(route = "conectar") { ConectarScreen(navController, auth) }

                            // Ruta Principal: MONITOR (Recibe IP validada)
                            composable(route = "home/{email}/{ipAddress}") { backStackEntry ->
                                val email = backStackEntry.arguments?.getString("email")
                                val ipAddress = backStackEntry.arguments?.getString("ipAddress") ?: "192.168.137.200"
                                HomeScreen(navController, email, ipAddress)
                            }

                            // Ruta Secundaria: JUEGO (Recibe IP validada)
                            composable("game/{email}/{ipAddress}") { backStackEntry ->
                                val email = backStackEntry.arguments?.getString("email") ?: "anonimo"
                                val ip = backStackEntry.arguments?.getString("ipAddress") ?: "0.0.0.0"
                                GameScreen(navController, email, ip)
                            }
                        }
                    }
                }
            }
        }
    }
}