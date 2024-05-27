package com.example.spotvibe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapaCreaEvento : AppCompatActivity() {
    private lateinit var mapView: MapView
    private var selectedLocation: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_crea_evento)

        // Inicializar osmdroid
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this))

        mapView = findViewById(R.id.mapView)
        val saveLocationButton = findViewById<Button>(R.id.saveLocationButton)

        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(0.0, 0.0)) // Centrar el mapa en una posición inicial

        mapView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val geoPoint = mapView.projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                if (selectedLocation != null) {
                    mapView.overlays.remove(selectedLocation)
                }
                selectedLocation = Marker(mapView).apply {
                    position = geoPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Ubicación seleccionada"
                }
                mapView.overlays.add(selectedLocation)
                mapView.invalidate()
            }
            true
        }

        saveLocationButton.setOnClickListener {
            if (selectedLocation != null) {
                val resultIntent = Intent().apply {
                    putExtra("selected_lat", selectedLocation!!.position.latitude)
                    putExtra("selected_lng", selectedLocation!!.position.longitude)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Por favor, selecciona una ubicación en el mapa", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
