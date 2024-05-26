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

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val recordame = findViewById<CheckBox>(R.id.checkbox_remember_me)
        val emailInput = findViewById<EditText>(R.id.edittext1)
        val passwordInput = findViewById<EditText>(R.id.edittext2)

        val intent = Intent(this, Home::class.java)
        val intent2 = Intent(this, CrearCuenta::class.java)
        val intent3 = Intent(this, ForgotPassword::class.java)
        val intent4 = Intent(this, HomeDuenio::class.java)

        val loginButton = findViewById<Button>(R.id.button_login)
        loginButton.setOnClickListener {
            if (validateInputs(emailInput, passwordInput)) {
                loginUser(emailInput.text.toString(), passwordInput.text.toString(), recordame, intent, intent4)
            }
        }

        val linkCreate = findViewById<TextView>(R.id.text_create_account)
        linkCreate.setOnClickListener {
            startActivity(intent2)
        }

        val linkForgot = findViewById<TextView>(R.id.textview_forgot_password)
        linkForgot.setOnClickListener {
            startActivity(intent3)
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

    private fun loginUser(email: String, password: String, recordame: CheckBox, intent: Intent, intent4: Intent) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "LOGIN EXITOSO", Toast.LENGTH_SHORT).show()
                    updateUI(user, recordame, intent, intent4)
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null, recordame, intent, intent4)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?, recordame: CheckBox, intent: Intent, intent4: Intent) {
        if (user != null) {
            getLocation()
            if (recordame.isChecked) {
                startActivity(intent4)
            } else {
                startActivity(intent)
            }
        }
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

   /* private fun updateEventDistances(currentLocation: LatLng) {
        EventProvider.listaEventos.forEach { evento ->
            val eventLocation = evento.MapsDistancia.split(", ").let {
                LatLng(it[0].toDouble(), it[1].toDouble())
            }
            val distance = calculateDistance(currentLocation, eventLocation)
            evento.distancia = "${distance}m"
        }
        // Actualiza tu interfaz de usuario aquí si es necesario
    }
*/
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
