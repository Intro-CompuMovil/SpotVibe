package com.example.spotvibe

data class Evento(
    var id: String? = null,
    val autor: String = "",
    val cantidadInscritos: Int = 0,
    val cantidadParticipantes: String = "",
    val detalles: String = "",
    val estado: String = "",
    val imagenUrl: String = "",
    val nombre: String = "",
    val emailCreador: String = ""
)