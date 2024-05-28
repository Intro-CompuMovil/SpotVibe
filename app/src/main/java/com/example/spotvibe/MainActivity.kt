package com.example.spotvibe

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.spotvibe.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val recordame = findViewById<CheckBox>(R.id.checkbox_remember_me)
        val emailInput = findViewById<EditText>(R.id.edittext1)
        val passwordInput = findViewById<EditText>(R.id.edittext2)

        val loginButton = findViewById<Button>(R.id.button_login)
        loginButton.setOnClickListener {
            if (validateInputs(emailInput, passwordInput)) {
                loginUser(emailInput.text.toString(), passwordInput.text.toString(), recordame)
            }
        }

        val linkCreate = findViewById<TextView>(R.id.text_create_account)
        linkCreate.setOnClickListener {
            startActivity(Intent(this, CrearCuenta::class.java))
        }

        val linkForgot = findViewById<TextView>(R.id.textview_forgot_password)
        linkForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Si el usuario ya está autenticado, verifica su rol y redirígelo
            getUserRoleAndRedirect(currentUser)
        }
    }

    private fun validateInputs(email: EditText, password: EditText): Boolean {
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()

        if (emailText.isEmpty()) {
            email.error = "Email is required"
            email.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.error = "Please enter a valid email"
            email.requestFocus()
            return false
        }

        if (passwordText.isEmpty()) {
            password.error = "Password is required"
            password.requestFocus()
            return false
        }

        if (passwordText.length < 6) {
            password.error = "Password must be at least 6 characters long"
            password.requestFocus()
            return false
        }

        return true
    }

    private fun loginUser(email: String, password: String, recordame: CheckBox) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "LOGIN EXITOSO", Toast.LENGTH_SHORT).show()
                    user?.let {
                        getUserRoleAndRedirect(it)
                        // Obtener y guardar el token FCM
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                saveTokenToDatabase(user.uid, token)
                            }
                        }
                    }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveTokenToDatabase(userId: String, token: String) {
        val userRef = database.child("users").child(userId)
        userRef.child("token").setValue(token)
    }

    private fun getUserRoleAndRedirect(user: FirebaseUser) {
        val userId = user.uid
        val userRef = database.child("users").child(userId).child("rol")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val role = dataSnapshot.getValue(String::class.java)
                if (role != null) {
                    if (role == "Dueño") {
                        startActivity(Intent(this@MainActivity, HomeDuenio::class.java))
                    } else {
                        startActivity(Intent(this@MainActivity, Home::class.java))
                    }
                    finish() // Cierra la MainActivity para evitar que el usuario vuelva a ella con el botón de retroceso
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, "Failed to retrieve user role.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLocation = LatLng(it.latitude, it.longitude)
                    //updateEventDistances(currentLocation)
                }
            }
        }
    }

    private fun calculateDistance(currentLocation: LatLng, eventLocation: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            currentLocation.latitude, currentLocation.longitude,
            eventLocation.latitude, eventLocation.longitude,
            results
        )
        return results[0] // Distancia en metros
    }
}
