package com.example.spotvibe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.Evento
import com.example.spotvibe.R

class Eventadapter (private val listaEventos:List<Evento>): RecyclerView.Adapter<EventViewholder> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewholder {
        val layoutInflater=LayoutInflater.from(parent.context)
        return EventViewholder(layoutInflater.inflate(R.layout.item_event),parent,false)

    }

    override fun getItemCount(): Int =listaEventos.size

    override fun onBindViewHolder(holder: EventViewholder, position: Int) {
        val item = listaEventos[position]
         holder.render(item)

    }
}