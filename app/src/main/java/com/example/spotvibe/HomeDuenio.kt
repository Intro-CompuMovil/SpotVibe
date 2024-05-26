package com.example.spotvibe

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.adapter.Eventadapter
import com.google.firebase.database.*

class HomeDuenio : AppCompatActivity() {
    private lateinit var eventAdapter: Eventadapter
    private lateinit var database: DatabaseReference
    private var eventos = mutableListOf<Evento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_duenio)
        database = FirebaseDatabase.getInstance().reference
        initRecyclerView()
        loadEventos()
        selecciondeEvento()

        val intent = Intent(this, Busqueda::class.java)
        val intent2 = Intent(this, Perfil::class.java)
        val intent3 = Intent(this, Notificaciones::class.java)
        val intent4 = Intent(this, eventosInscritos::class.java)
        val intent5 = Intent(this, DuenioCreaEvento::class.java)

        findViewById<TextView>(R.id.textviewCrearEvento).setOnClickListener {
            startActivity(intent5)
        }
        findViewById<ImageView>(R.id.imageView2x).setOnClickListener {
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.userbtnx).setOnClickListener {
            startActivity(intent2)
        }
        findViewById<ImageView>(R.id.notificationbtnx).setOnClickListener {
            startActivity(intent3)
        }
        findViewById<ImageView>(R.id.listeventbtnx).setOnClickListener {
            startActivity(intent4)
        }
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEventx)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = Eventadapter(eventos)
        recyclerView.adapter = eventAdapter
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
                    eventAdapter.updateList(eventos)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Maneja posibles errores.
            }
        })
    }

    private fun selecciondeEvento() {
        eventAdapter.setOnItemClickListener(object : Eventadapter.OnItemClickListener {
            override fun onItemClick(evento: Evento) {
                val intent = Intent(this@HomeDuenio, DetallesEvento::class.java).apply {
                    putExtra("nombreEvento", evento.nombre)
                    putExtra("autorEvento", evento.autor)
                    putExtra("fotoEvento", evento.imagenUrl)
                }
                startActivity(intent)
            }
        })
    }
}
