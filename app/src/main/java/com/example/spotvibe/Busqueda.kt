package com.example.spotvibe

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.adapter.Eventadapter
import com.google.firebase.database.*

class Busqueda : AppCompatActivity() {
    private lateinit var eventAdapter: Eventadapter
    private lateinit var database: DatabaseReference
    private lateinit var eventList: MutableList<Evento>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda)

        database = FirebaseDatabase.getInstance().reference
        eventList = mutableListOf()

        initRecyclerView()
        setupSearch()
        selecciondeEvento()
        loadEvents()
    }

    fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEvent2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = Eventadapter(emptyList())
        recyclerView.adapter = eventAdapter
    }

    private fun setupSearch() {
        val searchEditText = findViewById<EditText>(R.id.edittextbusqueda)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterEvents(s.toString())
            }
        })
    }

    private fun filterEvents(text: String) {
        if (::eventAdapter.isInitialized) {
            val filteredList = eventList.filter { evento ->
                evento.nombre.contains(text, ignoreCase = true) || evento.autor.contains(text, ignoreCase = true)
            }
            eventAdapter.updateList(filteredList)
        }
    }

    private fun selecciondeEvento() {
        eventAdapter.setOnItemClickListener(object : Eventadapter.OnItemClickListener {
            override fun onItemClick(evento: Evento) {
                val nombreEvento = evento.nombre
                val autorEvento = evento.autor
                val fotoEvento = evento.imagenUrl

                val intent = Intent(this@Busqueda, DetallesEvento::class.java)
                intent.putExtra("nombreEvento", nombreEvento)
                intent.putExtra("autorEvento", autorEvento)
                intent.putExtra("fotoEvento", fotoEvento)
                startActivity(intent)
            }
        })
    }

    private fun loadEvents() {
        val eventsRef = database.child("eventos")
        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    eventList.clear()
                    for (eventSnapshot in snapshot.children) {
                        val event = eventSnapshot.getValue(Evento::class.java)
                        event?.let { eventList.add(it) }
                    }
                    eventAdapter.updateList(eventList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}
