package com.example.seguridadapp.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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

// --- 1. ESTRUCTURA DE LOS DATOS ---
data class SensorData(
    val luz_promedio: Int = 0,
    val freq_promedio: Int = 0,
    val hz_sonido: Int = 0,
    val muestras: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// AHORA: Recibe "ipRecibida" (String) en lugar de un booleano
fun HomeScreen(navController: NavHostController, userEmail: String?, ipRecibida: String) {

    // --- ESTADOS ---
    var sensorData by remember { mutableStateOf(SensorData()) }
    var connectionStatus by remember { mutableStateOf("Iniciando transmisión...") }

    // UI Visual
    var volume by remember { mutableFloatStateOf(165f) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // FIREBASE
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("historial_sensores")

    // --- 2. LÓGICA AUTOMÁTICA (Se ejecuta al entrar) ---
    LaunchedEffect(key1 = ipRecibida) {
        withContext(Dispatchers.IO) {
            while (true) { // Bucle infinito mientras la pantalla esté viva
                try {
                    // CONSTRUCCIÓN SEGURA DE URL (ISO 27001)
                    val apiKey = "PROYECTO_THEREMIN_SECURE"
                    // Nos aseguramos que no haya espacios
                    val cleanIp = ipRecibida.trim()
                    val url = "http://$cleanIp/data?apiKey=$apiKey"

                    // PETICIÓN HTTP
                    val jsonResponse = URL(url).readText()
                    val newData = Gson().fromJson(jsonResponse, SensorData::class.java)

                    // ACTUALIZAR UI
                    withContext(Dispatchers.Main) {
                        sensorData = newData
                        connectionStatus = "EN VIVO - Transmisión Segura"
                    }

                    // GUARDAR EN FIREBASE (Auditoría)
                    if (newData.muestras > 0) {
                        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        val datosParaSubir = mapOf(
                            "luz" to newData.luz_promedio,
                            "frecuencia" to newData.freq_promedio,
                            "hz" to newData.hz_sonido,
                            "fecha" to timeStamp,
                            "usuario" to (userEmail ?: "anonimo")
                        )
                        myRef.push().setValue(datosParaSubir)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        connectionStatus = "Reconectando... (${e.message})"
                    }
                }
                delay(1000) // Actualizar cada 1 segundo
            }
        }
    }

    // --- 3. DISEÑO VISUAL ---

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú de Sistema", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                HorizontalDivider()

                // --- NUEVO BOTÓN: MODO JUEGO (GAMIFICACIÓN) ---
                NavigationDrawerItem(
                    label = { Text("Entrenamiento Auditivo") },
                    selected = false,
                    onClick = {
                        // Cerramos el menú y vamos al juego pasando los datos
                        scope.launch { drawerState.close() }
                        navController.navigate("game/$userEmail/${ipRecibida.trim()}")
                    }
                )

                // Opción vital: Permitir cambiar la IP si te mudas de red
                NavigationDrawerItem(
                    label = { Text("Desconectar / Cambiar IP") },
                    selected = false,
                    onClick = {
                        navController.navigate("conectar")
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Créditos") },
                    selected = false,
                    onClick = {
                        navController.navigate("creditos")
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Monitor Theremin", color = Color.White)
                            // Mostramos la IP conectada como confirmación visual
                            Text("Conectado a: $ipRecibida", color = Color.Green, fontSize = 12.sp)
                        }
                    },
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ESTADO DE CONEXIÓN
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = connectionStatus,
                            color = if (connectionStatus.contains("EN VIVO")) Color.Green else Color.Yellow,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // SENSORES (Visualización LDR)
                    Text("LECTURAS DE SENSORES", color = Color.Cyan, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        SensorBar(label = "Duración (A0)", value = sensorData.luz_promedio, max = 1023)
                        SensorBar(label = "Frecuencia (A1)", value = sensorData.freq_promedio, max = 1023)
                    }

                    // SECCIÓN SONIDO (Output)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("SALIDA DE AUDIO", color = Color.Cyan, fontWeight = FontWeight.Bold)

                    Box(modifier = Modifier.size(180.dp), contentAlignment = Alignment.Center) {
                        // Círculo de progreso visual
                        CircularProgressIndicator(
                            progress = { sensorData.hz_sonido / 1000f },
                            color = Color.Cyan,
                            strokeWidth = 10.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${sensorData.hz_sonido} Hz", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            Text("Sonando", color = Color.Gray, fontSize = 12.sp)
                        }
                    }

                    // SLIDER VOLUMEN (Visual)
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        valueRange = 0f..255f,
                        colors = SliderDefaults.colors(thumbColor = Color.Cyan, activeTrackColor = Color.Cyan)
                    )
                }
            }
        }
    }
}

// COMPONENTE PARA DIBUJAR LAS BARRAS
@Composable
fun SensorBar(label: String, value: Int, max: Int) {
    val safeMax = if (max == 0) 1 else max
    val heightRatio = (value.toFloat() / safeMax).coerceIn(0f, 1f)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.width(40.dp).height(150.dp).background(Color.DarkGray), contentAlignment = Alignment.BottomCenter) {
            Box(modifier = Modifier.fillMaxWidth().height((150 * heightRatio).dp).background(Color.Cyan))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(value.toString(), color = Color.Cyan, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}