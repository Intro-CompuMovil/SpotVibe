package com.example.spotvibe

class EventProvider {
    companion object{
        val listaEventos = listOf<Evento>(
            Evento("Concierto de Rock", "Juan Pérez", "url_photo_1", "0 km", "4.656066, -74.059591"), // Parque Simón Bolívar
            Evento("Exposición de Arte Moderno", "Ana Gómez", "url_photo_2", "0 km", "4.612639, -74.0705"), // Museo Botero
            Evento("Recital de Poesía", "Carlos Ruiz", "url_photo_3", "0 km", "4.598077, -74.076102"), // Teatro Colón
            Evento("Feria Tecnológica", "Diana Torres", "url_photo_4", "0 km", "4.645408, -74.078249"), // Corferias
            Evento("Carrera 5K por la Salud", "Mario Linares", "url_photo_5", "0 km", "4.655938, -74.055902"), // Parque Nacional
            Evento("Taller de Fotografía", "Luisa Fernández", "url_photo_6", "0 km", "4.629209, -74.064688"), // Planetario de Bogotá
            Evento("Festival Gastronómico", "Marcos Chávez", "url_photo_7", "0 km", "4.598892, -74.076157"), // Plaza de Mercado La Concordia
            Evento("Maratón de Programación", "Sofía Morales", "url_photo_8", "0 km", "4.602844, -74.064785"), // Universidad Nacional
            Evento("Torneo de Ajedrez", "Ernesto Díaz", "url_photo_9", "0 km", "4.609710, -74.081749"), // Biblioteca Luis Ángel Arango
            Evento("Conferencia sobre Cambio Climático", "Lucía Ramírez", "url_photo_10", "0 km", "4.601844, -74.066389") // Maloka
        )
    }
}