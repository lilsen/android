package com.example.seguridadapp.vistas


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, userEmail: String?, isConnected: Boolean) {
    // Estado simulado
    var volume by remember { mutableFloatStateOf(165f) }
    val signalStrength = 5
    val sensorA0 = 665
    val sensorA1 = 665

    // Fondo negro completo
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Encabezado superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Theremin Control", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { navController.navigate("conectar") }) {
                    Text("Conectar", color = Color.White, fontSize = 14.sp)
                }
            }

            // 🟢 Estado del dispositivo
            Text("ESTADO DEL DISPOSITIVO", color = Color.Cyan, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Status", color = Color.White)
                Text(
                    text = if (isConnected) "Conectado" else "Desconectado",
                    color = if (isConnected) Color.Green else Color.Red
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Potencia de Señal", color = Color.White)
                Row {
                    repeat(5) { i ->
                        Box(
                            modifier = Modifier
                                .size(width = 6.dp, height = (10 + i * 8).dp)
                                .background(if (isConnected) Color.Green else Color.Red)
                                .padding(end = 2.dp)
                        )
                    }
                }
            }


            // 🔊 Control de volumen
            Text("CONTROL DE VOLUMEN", color = Color.Cyan, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = volume / 255f,
                    color = Color.Cyan,
                    strokeWidth = 8.dp,
                    modifier = Modifier.fillMaxSize()
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(volume.toInt().toString(), color = Color.White, fontSize = 24.sp)
                    Text("0–255", color = Color.White, fontSize = 12.sp)
                }
            }
            Slider(
                value = volume,
                onValueChange = { volume = it },
                valueRange = 0f..255f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Cyan,
                    activeTrackColor = Color.Cyan,
                    inactiveTrackColor = Color.DarkGray
                )
            )

            // 📊 Sensores LDR
            Text("SENSORES LDR", color = Color.Cyan, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SensorBar(label = "A0", value = sensorA0, max = 1023)
                SensorBar(label = "A1", value = sensorA1, max = 1023)
            }
            // Botón para ir a créditos
            TextButton(onClick = { navController.navigate("creditos") }) {
                Text("Ver créditos de la app")
            }
            //salir de la app
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Cerrar app")
            }
        }
    }
}

@Composable
fun SensorBar(label: String, value: Int, max: Int) {
    val heightRatio = value.toFloat() / max
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height((100 * heightRatio).dp)
                .background(Color.Cyan)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Color.White)
        Text(value.toString(), color = Color.Cyan, fontSize = 12.sp)
    }
}

