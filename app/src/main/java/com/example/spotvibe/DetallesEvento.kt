package com.example.spotvibe

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

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

    private fun LlenarInfoDetalle() {
        val eventodetalle = findViewById<TextView>(R.id.textvieweventodetalles)
        val autordetalle = findViewById<TextView>(R.id.textviewautordetalles)
        val descripcionDetalle = findViewById<TextView>(R.id.textviewdescripcion)
        val cantidadParticipantes = findViewById<TextView>(R.id.textviewcantidadparticipantes)
        val cantidadInscritos = findViewById<TextView>(R.id.textviewcantidadinscritos)
        val estadoEvento = findViewById<TextView>(R.id.textviewestado)
        val imagenDetalle = findViewById<ImageView>(R.id.imageView3)

        val nombreEvento = intent.getStringExtra("nombreEvento")
        val autorEvento = intent.getStringExtra("autorEvento")
        val fotoEvento = intent.getStringExtra("fotoEvento")
        val detallesEvento = intent.getStringExtra("detallesEvento")
        val participantesEvento = intent.getStringExtra("cantidadParticipantes")
        val inscritosEvento = intent.getIntExtra("cantidadInscritos", 0)
        val estadoEventoStr = intent.getStringExtra("estadoEvento")

        eventodetalle.text = nombreEvento
        autordetalle.text = autorEvento
        descripcionDetalle.text = detallesEvento
        cantidadParticipantes.text = "Participantes: $participantesEvento"
        cantidadInscritos.text = "Inscritos: $inscritosEvento"
        estadoEvento.text = "Estado: $estadoEventoStr"

        if (!fotoEvento.isNullOrEmpty()) {
            Glide.with(this).load(fotoEvento).into(imagenDetalle)
        }
    }

    private fun EnviarAMisEventos() {
        // Código para inscribir al usuario en el evento
    }
}
