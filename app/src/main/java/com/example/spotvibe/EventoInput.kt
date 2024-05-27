package com.example.spotvibe

data class EventoInput(
    val nombre: String,
    val autor: String,
    val detalles: String,
    val cantidadParticipantes: String,
    val cantidadInscritos: Int,
    val estado: String,
    val imagenUrl: String,
    val localizacion: String,
    val emailCreador: String
)
