package com.example.seguridadapp.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, userEmail: String, ipAddress: String) {

    // --- VARIABLES DEL JUEGO ---
    val notasPosibles = listOf(420)
    var objetivoHz by remember { mutableIntStateOf(notasPosibles.random()) }
    var mensajeEstado by remember { mutableStateOf("¡Busca la nota!") }
    var juegoTerminado by remember { mutableStateOf(false) }

    // Variables de Puntuación
    var estrellasGanadas by remember { mutableIntStateOf(0) }
    var tiempoFinal by remember { mutableLongStateOf(0) }

    // Variables de sensores
    var currentHz by remember { mutableIntStateOf(0) }
    var tiempoInicio by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // UI & Navigation
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Firebase References
    val db = FirebaseDatabase.getInstance()
    val refJuegos = db.getReference("historial_juegos")
    val refAuditoria = db.getReference("auditoria_seguridad")

    // --- LÓGICA DEL BUCLE DE JUEGO ---
    LaunchedEffect(key1 = ipAddress, key2 = objetivoHz) {
        withContext(Dispatchers.IO) {
            while (!juegoTerminado) {
                try {
                    val cleanIp = ipAddress.trim()
                    val url = "http://$cleanIp/data?apiKey=PROYECTO_THEREMIN_SECURE"

                    val jsonResponse = URL(url).readText()
                    val data = Gson().fromJson(jsonResponse, SensorData::class.java)

                    withContext(Dispatchers.Main) {
                        currentHz = data.hz_sonido

                        // LÓGICA DE GANAR
                        val diferencia = abs(currentHz - objetivoHz)
                        if (diferencia < 30) {
                            juegoTerminado = true
                            mensajeEstado = "¡NIVEL COMPLETADO!"

                            // CÁLCULO DE ESTRELLAS Y TIEMPO
                            tiempoFinal = (System.currentTimeMillis() - tiempoInicio) / 1000

                            estrellasGanadas = when {
                                tiempoFinal < 5 -> 3 // Menos de 5s = 3 estrellas
                                tiempoFinal < 12 -> 2 // Menos de 12s = 2 estrellas
                                else -> 1
                            }

                            val fechaHoy = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                            // Guardar en Firebase con las estrellas
                            // Nota: Si quieres guardar las estrellas en la BD, agrégalo al modelo GameResult
                            val resultado = GameResult(userEmail, objetivoHz, tiempoFinal, fechaHoy)
                            refJuegos.push().setValue(resultado)

                            val log = AuditLog(userEmail, "Victoria ($estrellasGanadas Estrellas)", "Obj: $objetivoHz | Tiempo: ${tiempoFinal}s")
                            refAuditoria.push().setValue(log)
                        }

                        // ... dentro del LaunchedEffect en GameScreen.kt ...

                        if (diferencia < 30) {
                            juegoTerminado = true
                            mensajeEstado = "¡NIVEL COMPLETADO!"

                            // CÁLCULO DE ESTRELLAS Y TIEMPO
                            tiempoFinal = (System.currentTimeMillis() - tiempoInicio) / 1000

                            estrellasGanadas = when {
                                tiempoFinal < 5 -> 3
                                tiempoFinal < 12 -> 2
                                else -> 1
                            }

                            val fechaHoy = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                            // --- AQUÍ ESTÁ EL CAMBIO ---
                            // Agregamos "estrellas = estrellasGanadas" al guardar
                            val resultado = GameResult(
                                usuario = userEmail,
                                notaObjetivo = objetivoHz,
                                tiempoSegundos = tiempoFinal,
                                estrellas = estrellasGanadas, // <--- Guardamos las estrellas
                                fecha = fechaHoy
                            )
                            refJuegos.push().setValue(resultado)

                            // Auditoría también (opcional, pero queda bien)
                            val log = AuditLog(userEmail, "Victoria ($estrellasGanadas Estrellas)", "Obj: $objetivoHz | Tiempo: ${tiempoFinal}s")
                            refAuditoria.push().setValue(log)
                        }
                    }
                } catch (e: Exception) { }
                delay(200)
            }
        }
    }

    // --- INTERFAZ ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú de Juego", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Volver al Monitor") },
                    selected = false,
                    onClick = { navController.popBackStack() }
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = { navController.navigate("conectar") }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Entrenamiento Auditivo", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                color = Color.Black
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    if (juegoTerminado) {
                        // --- VISTA DE VICTORIA (3 ESTRELLAS) ---
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("¡EXCELENTE!", color = Color.Green, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))

                                // DIBUJAR ESTRELLAS
                                Row(horizontalArrangement = Arrangement.Center) {
                                    repeat(3) { index ->
                                        Icon(
                                            imageVector = if (index < estrellasGanadas) Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = null,
                                            tint = Color.Yellow,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Tiempo: ${tiempoFinal}s", color = Color.White, fontSize = 18.sp)
                                Text("Objetivo: $objetivoHz Hz", color = Color.Gray)

                                Spacer(modifier = Modifier.height(24.dp))

                                // BOTÓN REINTENTAR
                                Button(
                                    onClick = {
                                        objetivoHz = notasPosibles.random()
                                        tiempoInicio = System.currentTimeMillis()
                                        juegoTerminado = false
                                        mensajeEstado = "¡Busca la nota!"
                                        estrellasGanadas = 0
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("JUGAR OTRA VEZ", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                    } else {
                        // --- VISTA DE JUEGO ACTIVO ---
                        Text("OBJETIVO", color = Color.Gray, fontSize = 14.sp)
                        Text("$objetivoHz Hz", fontSize = 60.sp, fontWeight = FontWeight.Bold, color = Color.Cyan)

                        Spacer(modifier = Modifier.height(40.dp))

                        // Feedback Visual
                        Text("Tu nota actual:", color = Color.Gray)
                        Text("$currentHz Hz", color = Color.White, fontSize = 32.sp)

                        val diff = abs(currentHz - objetivoHz).toFloat()
                        // Barra se llena mientras más cerca estás (rango 200hz)
                        val cercania = (1f - (diff / 200f)).coerceIn(0f, 1f)

                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { cercania },
                            modifier = Modifier.fillMaxWidth().height(20.dp),
                            color = if(cercania > 0.85f) Color.Green else Color.Red,
                            trackColor = Color.DarkGray
                        )

                        if (cercania > 0.85f) {
                            Text("¡Casi lo tienes!", color = Color.Green, modifier = Modifier.padding(top=8.dp))
                        }
                    }
                }
            }
        }
    }
}