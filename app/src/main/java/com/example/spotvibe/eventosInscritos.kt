package com.example.spotvibe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class eventosInscritos : AppCompatActivity() {

    var datos = arrayOf("Burger Master", "Santa fe Vs Millonarios", "Maraton", "Torneo fifa")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_inscritos)

       val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, datos)
       val listView: ListView = findViewById(R.id.lista)
       listView.adapter=adapter
    }
}