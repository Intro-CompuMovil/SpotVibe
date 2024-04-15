package com.example.spotvibe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotvibe.adapter.Eventadapter

class HomeDuenio : AppCompatActivity() {
    private lateinit var eventAdapter: Eventadapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_duenio)
        initRecyclerView()
        selecciondeEvento()
        val intent= Intent(this, Busqueda::class.java)
        val intent2= Intent(this, Perfil::class.java)
        val intent3= Intent(this, Notificaciones::class.java)
        val intent4= Intent(this, eventosInscritos::class.java)
        val intent5= Intent(this, DuenioCreaEvento::class.java)
        val imageButton3=findViewById<ImageView>(R.id.notificationbtnx)
        val imageButton4=findViewById<ImageView>(R.id.listeventbtnx)
        val imageButton2=findViewById<ImageView>(R.id.userbtnx)
        val imageButton=findViewById<ImageView>(R.id.imageView2x)
        val textButton5=findViewById<TextView>(R.id.textviewCrearEvento)
        textButton5.setOnClickListener{
            startActivity(intent5)
        }
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
    }

    fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEventx)
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


                val intent = Intent(this@HomeDuenio, DetallesEvento::class.java)


                intent.putExtra("nombreEvento", nombreEvento)
                intent.putExtra("autorEvento", autorEvento)
                intent.putExtra("fotoEvento", fotoEvento)


                startActivity(intent)
            }
        })



    }
}