package com.example.spotvibe

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.adapter.Eventadapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class HomeDuenio : AppCompatActivity() {
    private lateinit var eventAdapter: Eventadapter
    private lateinit var database: DatabaseReference
    private var eventos = mutableListOf<Evento>()
    private var emailduenio: String? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_duenio)
        database = FirebaseDatabase.getInstance().reference
        emailduenio = intent.getStringExtra("user_email")
        initRecyclerView()
        loadEventos()
        selecciondeEvento()
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userId = currentUser.uid // Guardar el ID del usuario
        }

        val intent = Intent(this, Busqueda::class.java)
        val intent2 = Intent(this, Perfil::class.java)
        val intent3 = Intent(this, Notificaciones::class.java)
        val intent4 = Intent(this, eventosInscritos::class.java)
        val intent5 = Intent(this, DuenioCreaEvento::class.java)

        findViewById<TextView>(R.id.textviewCrearEvento).setOnClickListener {
            intent5.putExtra("user_email", emailduenio)
            intent5.putExtra("user_id", userId)
            startActivity(intent5)
        }
        findViewById<ImageView>(R.id.imageView2x).setOnClickListener {
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.userbtnx).setOnClickListener {
            startActivity(intent2)
        }
        findViewById<ImageView>(R.id.notificationbtnx).setOnClickListener {
            intent3.putExtra("user_email", emailduenio)
            startActivity(intent3)
        }
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEventx)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = Eventadapter(eventos)
        recyclerView.adapter = eventAdapter
    }

    private fun loadEventos() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return // Obtener el ID del usuario desde FirebaseAuth

        val eventsRef = database.child("eventos")
        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    eventos.clear()
                    for (eventSnapshot in snapshot.children) {
                        val eventId = eventSnapshot.key // Obtener el ID del evento
                        val event = eventSnapshot.getValue(Evento::class.java)?.apply {
                            this.id = eventId // Establecer el ID del evento
                        }
                        event?.let {
                            if (it.idCreador == userId) { // Comparar por ID del usuario
                                eventos.add(it)
                            }
                        }
                    }
                    eventAdapter.updateList(eventos)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar posibles errores.
            }
        })
    }

    private fun selecciondeEvento() {
        eventAdapter.setOnItemClickListener(object : Eventadapter.OnItemClickListener {
            override fun onItemClick(evento: Evento) {
                val intent = Intent(this@HomeDuenio, ModificaEvento::class.java).apply {
                    putExtra("eventId", evento.id)
                    putExtra("nombreEvento", evento.nombre)
                    putExtra("autorEvento", evento.autor)
                    putExtra("fotoEvento", evento.imagenUrl)
                    putExtra("detalleEvento", evento.detalles)
                    putExtra("cantidadParticipantes", evento.cantidadParticipantes)
                }
                startActivity(intent)
            }
        })
    }
}
