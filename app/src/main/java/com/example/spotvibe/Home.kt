package com.example.spotvibe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.adapter.Eventadapter

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initRecyclerView()
        val intent= Intent(this, Busqueda::class.java)
        val imageButton=findViewById<ImageView>(R.id.imageView2)
        imageButton.setOnClickListener{
            startActivity(intent)
        }
    }

    fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEvent)
        recyclerView.layoutManager= LinearLayoutManager(this)
        recyclerView.adapter=Eventadapter(EventProvider.listaEventos)
    }
}