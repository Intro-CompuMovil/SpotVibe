package com.example.spotvibe

data class EventoInput(
    val nombre: String = "",
    val autor: String = "",
    val detalles: String = "",
    val cantidadParticipantes: String = "",
    val cantidadInscritos: Int = 0,
    val estado: String = "PENDIENTE",
    val imagenUrl: String = ""
)
