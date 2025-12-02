package com.example.seguridadapp.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditosScreen(navController: NavController, auth: FirebaseAuth) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Créditos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Theremii es una aplicación móvil inspirada en el fascinante instrumento electrónico conocido como theremin, uno de los primeros en utilizarse en la música. Su objetivo es acercar a los usuarios al mundo de la música experimental, permitiendo crear sonidos mediante gestos y movimientos, sin necesidad de contacto físico con el dispositivo.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "La app busca combinar arte, tecnología e interacción, ofreciendo una experiencia sensorial que despierta la curiosidad por la música instrumental y el control por proximidad.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Inspiración:\n\nEl proyecto surge del interés por la relación entre el cuerpo humano y la creación sonora, explorando cómo la tecnología moderna puede reinterpretar instrumentos de música clásica. Asimismo, se rinde homenaje a la inventiva y a la creatividad de León Theremin, inventor del instrumento original, y a la evolución de la música digital.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Desarrolladores: spd only",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Light
                )
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Volver")
            }
        }
    }
}
