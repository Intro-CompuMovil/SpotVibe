package com.example.spotvibe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.adapter.Eventadapter

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initRecyclerView()
    }
    fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEvent)
        recyclerView.layoutManager= LinearLayoutManager(this)
        recyclerView.adapter=Eventadapter(EventProvider.listaEventos)
    }
}