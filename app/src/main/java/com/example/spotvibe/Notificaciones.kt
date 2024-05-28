package com.example.spotvibe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Notificaciones : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var notificacionesList: MutableList<String>
    private lateinit var database: DatabaseReference
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)

        notificacionesList = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notificacionesList)

        listView = findViewById(R.id.lista)
        listView.adapter = adapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userId = currentUser.uid // Obtener el ID del usuario autenticado
            database = FirebaseDatabase.getInstance().reference.child("notificaciones")
            loadNotificaciones()
        } else {
            Log.e("Notificaciones", "Usuario no autenticado.")
            // Handle unauthenticated user scenario
        }
    }

    private fun loadNotificaciones() {
        if (userId == null) {
            Log.e("Notificaciones", "UserId is null.")
            return
        }

        val query = database.orderByChild("userId").equalTo(userId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificacionesList.clear()
                for (notificacionSnapshot in snapshot.children) {
                    val notificacion = notificacionSnapshot.getValue(Notificacion::class.java)
                    notificacion?.let {
                        notificacionesList.add(it.mensaje)
                        Log.d("Notificaciones", "Notificación agregada: ${it.mensaje}")
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Notificaciones", "Error al cargar notificaciones: ${error.message}")
            }
        })
    }
}
