package com.example.spotvibe

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DetallesEvento : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_evento)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

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
        val eventId = intent.getStringExtra("eventId")

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
        val currentUser = auth.currentUser
        val eventId = intent.getStringExtra("eventId")
        val nombreEvento = intent.getStringExtra("nombreEvento")
        val autorEvento = intent.getStringExtra("autorEvento")
        val fotoEvento = intent.getStringExtra("fotoEvento")
        val detallesEvento = intent.getStringExtra("detallesEvento")
        val participantesEvento = intent.getStringExtra("cantidadParticipantes")
        val inscritosEvento = intent.getIntExtra("cantidadInscritos", 0)
        val estadoEventoStr = intent.getStringExtra("estadoEvento")

        if (currentUser != null && eventId != null) {
            val userId = currentUser.uid
            val userEventRef = database.child("userEvents").child(userId).child(eventId)

            // Crear el registro del evento para el usuario
            val userEventMap = mapOf(
                "nombreEvento" to nombreEvento,
                "autorEvento" to autorEvento,
                "fotoEvento" to fotoEvento,
                "detallesEvento" to detallesEvento,
                "participantesEvento" to participantesEvento,
                "inscritosEvento" to inscritosEvento,
                "estadoEvento" to estadoEventoStr
            )

            // Guardar el registro del usuario al evento
            userEventRef.setValue(userEventMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Incrementar la cantidad de inscritos
                    val eventRef = database.child("eventos").child(eventId)
                    eventRef.child("cantidadInscritos").runTransaction(object : Transaction.Handler {
                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            val currentValue = mutableData.getValue(Int::class.java) ?: 0
                            mutableData.value = currentValue + 1
                            return Transaction.success(mutableData)
                        }

                        override fun onComplete(
                            databaseError: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                        ) {
                            if (databaseError != null) {
                                Toast.makeText(
                                    this@DetallesEvento,
                                    "Error al inscribir al evento: ${databaseError.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (committed) {
                                Toast.makeText(
                                    this@DetallesEvento,
                                    "Inscripción exitosa al evento: $nombreEvento",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
                } else {
                    Toast.makeText(
                        this,
                        "Error al inscribir al evento: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "Error al obtener la información del evento o usuario.", Toast.LENGTH_SHORT).show()
        }
    }
}
