package com.example.spotvibe.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotvibe.Evento
import com.example.spotvibe.R

class EventViewholder(view: View): RecyclerView.ViewHolder(view) {
    val evento = view.findViewById<TextView>(R.id.textViewTitle)
    val creador = view.findViewById<TextView>(R.id.textViewName)
    val distanciaa = view.findViewById<TextView>(R.id.textViewDistancia)
    val eventImage = view.findViewById<ImageView>(R.id.eventImage)
    val fechaa = view.findViewById<TextView>(R.id.textViewfecha)
    fun render(eventoModel: Evento) {
        evento.text = eventoModel.nombre
        creador.text = eventoModel.autor
        distanciaa.text = eventoModel.cantidadInscritos.toString() // Actualiza según el campo que desees mostrar
        fechaa.text = eventoModel.fecha
        // Cargar la imagen del evento utilizando Glide
        Glide.with(eventImage.context)
            .load(eventoModel.imagenUrl)
            .placeholder(R.drawable.ic_map) // Imagen de marcador de posición
            .into(eventImage)
    }
}
