package com.example.spotvibe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class Splash : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Si el usuario ya está autenticado, verifica su rol y redirígelo
            getUserRoleAndRedirect(currentUser)
        } else {
            // Redirigir a la pantalla de inicio de sesión
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun getUserRoleAndRedirect(user: FirebaseUser) {
        val userId = user.uid
        val userRef = database.child("users").child(userId).child("rol")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val role = dataSnapshot.getValue(String::class.java)
                if (role != null) {
                    if (role == "Dueño") {
                        startActivity(Intent(this@Splash, HomeDuenio::class.java))
                    } else {
                        startActivity(Intent(this@Splash, Home::class.java))
                    }
                }
                finish() // Cierra la SplashActivity
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, "Failed to retrieve user role.", Toast.LENGTH_SHORT).show()
                // Redirigir a la pantalla de inicio de sesión en caso de error
                startActivity(Intent(this@Splash, MainActivity::class.java))
                finish()
            }
        })
    }
}
