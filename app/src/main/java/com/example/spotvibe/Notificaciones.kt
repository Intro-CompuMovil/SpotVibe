package com.example.spotvibe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.database.*

class Notificaciones : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var notificacionesList: MutableList<String>
    private lateinit var database: DatabaseReference
    private lateinit var emailCreador: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)

        emailCreador = intent.getStringExtra("user_email").toString()
        notificacionesList = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notificacionesList)

        listView = findViewById(R.id.lista)
        listView.adapter = adapter

        database = FirebaseDatabase.getInstance().reference.child("notificaciones")
        loadNotificaciones()
    }

    private fun loadNotificaciones() {
        val query = database.orderByChild("email").equalTo(emailCreador)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificacionesList.clear()
                for (notificacionSnapshot in snapshot.children) {
                    val notificacion = notificacionSnapshot.getValue(Notificacion::class.java)
                    notificacion?.let {
                        notificacionesList.add(it.mensaje)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
            }
        })
    }
}
