package com.example.spotvibe

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class Eventos : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private var eventos = mutableListOf<Evento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eventos)

        listView = findViewById(R.id.listView)
        database = FirebaseDatabase.getInstance().reference

        loadEventos()
    }

    private fun loadEventos() {
        val eventsRef = database.child("eventos")
        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    eventos.clear()
                    for (eventSnapshot in snapshot.children) {
                        val event = eventSnapshot.getValue(Evento::class.java)
                        event?.let { eventos.add(it) }
                    }
                    initListView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Maneja posibles errores.
                Toast.makeText(this@Eventos, "Error loading events: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initListView() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eventos.map { it.nombre })
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedEvent = eventos[position]
            Toast.makeText(this, "Selected: ${selectedEvent.nombre}", Toast.LENGTH_SHORT).show()
            val bundle = Bundle().apply {
                putString("nombre", selectedEvent.nombre)
                putString("autor", selectedEvent.autor)
                putString("imagenUrl", selectedEvent.imagenUrl)
                putString("Localizacion", selectedEvent.localizacion)
                //putString
                //putString("cantidadInscritos", selectedEvent.cantidadInscritos.toString())
                //putString("cantidadInscritos", selectedEvent.cantidadInscritos.toString())

            }
            val intent = Intent(this, Mapa::class.java)
            intent.putExtra("informacionLugar", bundle)
            startActivity(intent)
        }
    }
}
