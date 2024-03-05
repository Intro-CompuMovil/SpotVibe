package com.example.spotvibe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class Notificaciones : AppCompatActivity() {

    var datos = arrayOf("El evento de fifa del 28 de enero se ha cancelado", "Se acerca el partido del Real madrid", "Promociones en Andres carne de res")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, datos)
        val listView: ListView = findViewById(R.id.lista)
        listView.adapter=adapter
    }
}