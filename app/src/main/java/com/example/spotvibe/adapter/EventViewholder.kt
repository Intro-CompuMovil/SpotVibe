package com.example.spotvibe.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.Evento
import com.example.spotvibe.R

class EventViewholder (view:View): RecyclerView.ViewHolder(view){

    val evento = view.findViewById<TextView>(R.id.textViewTitle)
    val creador = view.findViewById<TextView>(R.id.textViewName)
    val distanciaa = view.findViewById<TextView>(R.id.textViewDistancia)
    fun render(eventoModel:Evento){
        evento.text=eventoModel.name
        creador.text=eventoModel.Autor
        distanciaa.text=eventoModel.distancia
    }
}