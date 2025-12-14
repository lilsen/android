package com.example.seguridadapp.vistas

// 1. MODELO DE AUDITORÍA (ISO 27001 - A.12.4 Registro de eventos)
// Guardaremos QUÉ pasó, QUIÉN lo hizo y CUÁNDO.
data class AuditLog(
    val usuario: String = "anonimo",
    val accion: String = "",       // Ej: "Victoria en Juego", "Inicio Sesión"
    val detalle: String = "",      // Ej: "Puntaje: 100", "IP: 192.168..."
    val timestamp: Long = System.currentTimeMillis() // Hora automática del sistema
)

// 2. MODELO DE RESULTADO DE JUEGO
data class GameResult(
    val usuario: String = "",
    val notaObjetivo: Int = 0,     // La frecuencia que debía alcanzar
    val tiempoSegundos: Long = 0,  // Cuánto tardó en lograrlo
    val fecha: String = "",
    val estrellas: Int = 0,    // <--- ¡NUEVO CAMPO!
)

