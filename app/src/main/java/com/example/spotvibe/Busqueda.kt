package com.example.spotvibe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.adapter.Eventadapter

class Busqueda : AppCompatActivity() {
    private lateinit var eventAdapter: Eventadapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda)
        initRecyclerView()
        val imagenbusqueda = findViewById<ImageView>(R.id.imageViewbusqueda)
        setupSearch()

    }
    fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEvent2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = Eventadapter(EventProvider.listaEventos) // Asegura que esto se ejecute antes de setupSearch
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
        if(::eventAdapter.isInitialized) { // Verifica si eventAdapter ha sido inicializado
            val filteredList = EventProvider.listaEventos.filter { evento ->
                evento.name.contains(text, ignoreCase = true) || evento.Autor.contains(text, ignoreCase = true)
            }
            eventAdapter.updateList(filteredList)
        }
    }
}