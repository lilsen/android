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
import com.example.seguridadapp.vistas.HomeScreen
import com.example.seguridadapp.vistas.LoginScreen
import com.example.seguridadapp.vistas.RegisterScreen
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //inicializacion de firebase

        auth = FirebaseAuth.getInstance()

        setContent {
              SeguridadAppTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding( paddingValues = innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = "login"
                        ) {
                            composable( route = "login"){ LoginScreen(navController, auth) }
                            composable ( route = "register") { RegisterScreen(navController, auth) }
                            // Aquí nos manda al HomeScreen recibiendo el email para decirle hola {email}
                            composable(route = "home/{email}?connected={connected}") {
                                    backStackEntry ->
                                val email = backStackEntry.arguments?.getString("email")
                                val connected = backStackEntry.arguments?.getString("connected") ?: "false"
                                HomeScreen(navController, email, connected == "true")
                            }
                            composable ( route = "creditos") { CreditosScreen(navController, auth) }
                            composable ( route = "conectar") { ConectarScreen(navController, auth) }


                        }
                    }
                }
            }
        }
    }
}