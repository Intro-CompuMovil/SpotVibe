package com.example.spotvibe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class eventosInscritos : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var eventosInscritosList = mutableListOf<String>()
    private var eventosMap = mutableMapOf<String, Evento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_inscritos)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        listView = findViewById(R.id.lista)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eventosInscritosList)
        listView.adapter = adapter

        loadEventosInscritos()

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedEventName = eventosInscritosList[position]
            val selectedEvent = eventosMap[selectedEventName]
            selectedEvent?.let {
                val intent = Intent(this, DetallesEvento::class.java).apply {
                    putExtra("eventId", it.id)
                    putExtra("nombreEvento", it.nombre)
                    putExtra("autorEvento", it.autor)
                    putExtra("fotoEvento", it.imagenUrl)
                    putExtra("detallesEvento", it.detalles)
                    putExtra("cantidadParticipantes", it.cantidadParticipantes)
                    putExtra("cantidadInscritos", it.cantidadInscritos)
                    putExtra("estadoEvento", it.estado)
                }
                startActivity(intent)
            } ?: Toast.makeText(this, "No se encontró la información del evento", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadEventosInscritos() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userEventsRef = database.child("userEvents").child(userId)

            userEventsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    eventosInscritosList.clear()
                    eventosMap.clear()
                    Log.d("eventosInscritos", "DataSnapshot size: ${snapshot.childrenCount}")
                    for (eventSnapshot in snapshot.children) {
                        val evento = eventSnapshot.getValue(Evento::class.java)
                        Log.d("eventosInscritos", "Evento encontrado: ${evento?.nombre}")
                        evento?.let {
                            it.id = eventSnapshot.key.toString() // Asignar el ID del evento
                            eventosInscritosList.add(it.nombre)
                            eventosMap[it.nombre] = it
                        }
                    }
                    Log.d("eventosInscritos", "Eventos cargados: ${eventosInscritosList.size}")
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@eventosInscritos, "Error al cargar eventos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}
