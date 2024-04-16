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
    private lateinit var eventAdapter: Eventadapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initRecyclerView()
        selecciondeEvento()
        val intent= Intent(this, Busqueda::class.java)
        val intent2= Intent(this, Perfil::class.java)
        val intent3= Intent(this, Notificaciones::class.java)
        val intent4= Intent(this, eventosInscritos::class.java)
        val intent5= Intent(this, Eventos::class.java)
        val imageButton3=findViewById<ImageView>(R.id.notificationbtn)
        val imageButton4=findViewById<ImageView>(R.id.listeventbtn)
        val imageButton2=findViewById<ImageView>(R.id.userbtn)
        val imageButton=findViewById<ImageView>(R.id.imageView2)
        val imageUbicacion=findViewById<ImageView>(R.id.imageV)
        imageButton.setOnClickListener{
            startActivity(intent)
        }
        imageButton2.setOnClickListener{
            startActivity(intent2)
        }
        imageButton3.setOnClickListener{
            startActivity(intent3)
        }
        imageButton4.setOnClickListener{
            startActivity(intent4)
        }
        imageUbicacion.setOnClickListener(){
            startActivity(intent5)
        }
    }

    fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEvent)
        recyclerView.layoutManager= LinearLayoutManager(this)
        eventAdapter = Eventadapter(EventProvider.listaEventos)
        recyclerView.adapter=eventAdapter
    }
    private fun selecciondeEvento() {

        eventAdapter.setOnItemClickListener(object : Eventadapter.OnItemClickListener {
            override fun onItemClick(evento: Evento) {

                val nombreEvento = evento.name
                val autorEvento = evento.Autor
                val fotoEvento = evento.photo


                val intent = Intent(this@Home, DetallesEvento::class.java)


                intent.putExtra("nombreEvento", nombreEvento)
                intent.putExtra("autorEvento", autorEvento)
                intent.putExtra("fotoEvento", fotoEvento)


                startActivity(intent)
            }
        })



    }
}