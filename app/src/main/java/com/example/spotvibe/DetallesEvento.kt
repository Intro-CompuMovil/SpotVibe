package com.example.spotvibe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DetallesEvento : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_evento)
        LlenarInfoDetalle()
        val botoninscribirse = findViewById<Button>(R.id.botoninscribirsealevento)
        botoninscribirse.setOnClickListener {

            EnviarAMisEventos()
            it.isEnabled = false
        }



    }

    fun LlenarInfoDetalle(){
        val eventodetalle = findViewById<TextView>(R.id.textvieweventodetalles)
        val autordetalle = findViewById<TextView>(R.id.textviewautordetalles)
        val nombreEvento = intent.getStringExtra("nombreEvento")
        val autorEvento = intent.getStringExtra("autorEvento")
        val fotoEvento = intent.getStringExtra("fotoEvento")
        eventodetalle.text = nombreEvento
        autordetalle.text = autorEvento
    }
    fun EnviarAMisEventos(){
        val nombreEvento = intent.getStringExtra("nombreEvento")
        val autorEvento = intent.getStringExtra("autorEvento")
        val fotoEvento = intent.getStringExtra("fotoEvento")
       /*val intent = Intent(this@DetallesEvento, EventosInscritos::class.java)
        intent.putExtra("nombreEvento", nombreEvento)
                intent.putExtra("autorEvento", autorEvento)
                intent.putExtra("fotoEvento", fotoEvento)


                startActivity(intent) */


    }
}