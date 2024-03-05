package com.example.spotvibe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.Evento
import com.example.spotvibe.R

class Eventadapter(private var listaEventos: List<Evento>) :
    RecyclerView.Adapter<EventViewholder>() {

    // Definir un oyente de clics
    private var listener: OnItemClickListener? = null

    // Interfaz para el oyente de clics
    interface OnItemClickListener {
        fun onItemClick(evento: Evento)
    }

    // Método para establecer el oyente de clics desde fuera del adaptador
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewholder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EventViewholder(layoutInflater.inflate(R.layout.item_event, parent, false))
    }

    override fun getItemCount(): Int = listaEventos.size

    override fun onBindViewHolder(holder: EventViewholder, position: Int) {
        val item = listaEventos[position]
        holder.render(item)

        // Establecer el clic en el elemento
        holder.itemView.setOnClickListener {
            listener?.onItemClick(item)
        }
    }

    // Función para actualizar la lista de eventos
    fun updateList(newList: List<Evento>) {
        listaEventos = newList
        notifyDataSetChanged()
    }
}