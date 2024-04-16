package com.example.spotvibe
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
        val Recordame = findViewById<CheckBox>(R.id.checkbox_remember_me)
        Recordame.isEnabled
        val intent= Intent(this, Home::class.java)

        val intent2= Intent(this, CrearCuenta::class.java)
        val intent3=Intent(this, ForgotPassword::class.java)
        val intent4= Intent(this, HomeDuenio::class.java)
        val imageButton=findViewById<Button>(R.id.button_login)
        imageButton.setOnClickListener{
            if(Recordame.isChecked){
                startActivity(intent4)
            }
            else {
                startActivity(intent)
            }
        }
        val linkCreate =findViewById<TextView>(R.id.text_create_account)
        linkCreate.setOnClickListener {
            startActivity(intent2)
        }
        val linkforgot=findViewById<TextView>(R.id.textview_forgot_password)
        linkforgot.setOnClickListener {
            startActivity(intent3)
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLocation = LatLng(it.latitude, it.longitude)
                    updateEventDistances(currentLocation)
                }
            }
        }
    }

    private fun updateEventDistances(currentLocation: LatLng) {
        EventProvider.listaEventos.forEach { evento ->
            val eventLocation = evento.MapsDistancia.split(", ").let {
                LatLng(it[0].toDouble(), it[1].toDouble())
            }
            val distance = calculateDistance(currentLocation, eventLocation)
            evento.distancia = "${distance}m"
        }
        // Actualiza tu interfaz de usuario aquí si es necesario
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