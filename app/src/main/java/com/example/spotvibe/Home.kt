package com.example.spotvibe

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotvibe.adapter.Eventadapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Home : AppCompatActivity() {
    private lateinit var eventAdapter: Eventadapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userNameTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initRecyclerView()
        selecciondeEvento()

        userNameTextView = findViewById(R.id.tv_user_name)
        profileImageView = findViewById(R.id.profile_image)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = database.child("users").child(userId)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userName = snapshot.child("name").value.toString()
                        val profileImageUrl = snapshot.child("profileImageUrl").value.toString()

                        userNameTextView.text = userName
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this@Home).load(profileImageUrl).into(profileImageView)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }

        val intent = Intent(this, Busqueda::class.java)
        val intent2 = Intent(this, Perfil::class.java)
        val intent3 = Intent(this, Notificaciones::class.java)
        val intent4 = Intent(this, eventosInscritos::class.java)
        val intent5 = Intent(this, Eventos::class.java)
        val imageButton3 = findViewById<ImageView>(R.id.notificationbtn)
        val imageButton4 = findViewById<ImageView>(R.id.listeventbtn)
        val imageButton2 = findViewById<ImageView>(R.id.userbtn)
        val imageButton = findViewById<ImageView>(R.id.imageView2)
        val imageUbicacion = findViewById<ImageView>(R.id.imageV)
        imageButton.setOnClickListener {
            startActivity(intent)
        }
        imageButton2.setOnClickListener {
            startActivity(intent2)
        }
        imageButton3.setOnClickListener {
            startActivity(intent3)
        }
        imageButton4.setOnClickListener {
            startActivity(intent4)
        }
        imageUbicacion.setOnClickListener() {
            startActivity(intent5)
        }

        loadEvents()
    }

    fun initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerEvent)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = Eventadapter(emptyList())
        recyclerView.adapter = eventAdapter
    }

    private fun loadEvents() {
        val eventsRef = database.child("eventos")

        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val eventList = mutableListOf<Evento>()
                    for (eventSnapshot in snapshot.children) {
                        val eventId = eventSnapshot.key ?: ""
                        val event = eventSnapshot.getValue(Evento::class.java)?.copy(id = eventId)
                        if (event?.estado == "ACEPTADA") {
                            eventList.add(event)
                        }
                    }
                    eventAdapter.updateList(eventList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun selecciondeEvento() {
        eventAdapter.setOnItemClickListener(object : Eventadapter.OnItemClickListener {
            override fun onItemClick(evento: Evento) {
                val intent = Intent(this@Home, DetallesEvento::class.java)
                intent.putExtra("eventId", evento.id)
                intent.putExtra("nombreEvento", evento.nombre)
                intent.putExtra("autorEvento", evento.autor)
                intent.putExtra("fotoEvento", evento.imagenUrl)
                intent.putExtra("detallesEvento", evento.detalles)
                intent.putExtra("cantidadParticipantes", evento.cantidadParticipantes)
                intent.putExtra("cantidadInscritos", evento.cantidadInscritos)
                intent.putExtra("estadoEvento", evento.estado)
                startActivity(intent)
            }
        })
    }
}
