package com.example.spotvibe
import android.content.Intent
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
        setupSearch()
        selecciondeEvento()

    }
    fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEvent2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = Eventadapter(EventProvider.listaEventos)
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
        if(::eventAdapter.isInitialized) {
            val filteredList = EventProvider.listaEventos.filter { evento ->
                evento.name.contains(text, ignoreCase = true) || evento.Autor.contains(text, ignoreCase = true)
            }
            eventAdapter.updateList(filteredList)
        }
    }

    private fun selecciondeEvento() {

        eventAdapter.setOnItemClickListener(object : Eventadapter.OnItemClickListener {
            override fun onItemClick(evento: Evento) {

                val nombreEvento = evento.name
                val autorEvento = evento.Autor
                val fotoEvento = evento.photo


                val intent = Intent(this@Busqueda, DetallesEvento::class.java)


                intent.putExtra("nombreEvento", nombreEvento)
                intent.putExtra("autorEvento", autorEvento)
                intent.putExtra("fotoEvento", fotoEvento)


                startActivity(intent)
            }
        })



    }

}