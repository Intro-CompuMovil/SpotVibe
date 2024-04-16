package com.example.spotvibe

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Eventos : AppCompatActivity() {

    private lateinit var listView: ListView
    private var eventos = mutableListOf<Evento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eventos)

        listView = findViewById(R.id.listView)
        initEventos()
        initListView()
    }

    private fun initEventos() {
        eventos = EventProvider.listaEventos.toMutableList()
    }

    private fun initListView() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eventos.map { it.name })
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedEvent = eventos[position]
            Toast.makeText(this, "Selected: ${selectedEvent.name}", Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putString("nombre", selectedEvent.name)
            bundle.putString("autor", selectedEvent.Autor)
            bundle.putString("nombre", selectedEvent.photo)
            bundle.putString("distancia", selectedEvent.distancia)
            val parts = selectedEvent.MapsDistancia.split(",")
            val latitud = parts[0].trim().toDouble()
            val longitud = parts[1].trim().toDouble()
            bundle.putDouble("latitud", latitud)
            bundle.putDouble("longitud", longitud)
            val intent = Intent(this, Mapa::class.java)
            intent.putExtra("informacionLugar", bundle)
            startActivity(intent)
        }
    }
}

