package com.example.spotvibe

import android.app.Activity
import android.view.View
import android.view.MotionEvent
import android.app.UiModeManager
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay

class MapaCreaEvento : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var mapView: MapView
    private var selectedLocation: Marker? = null
    lateinit var roadManager: RoadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_crea_evento)

        // Inicializar osmdroid
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this))

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        roadManager = OSRMRoadManager(this, "ANDROID")
        val saveLocationButton = findViewById<Button>(R.id.saveLocationButton)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mapView.setOnTouchListener(object : View.OnTouchListener {
            private var lastTouchX: Float = 0f
            private var lastTouchY: Float = 0f

            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            lastTouchX = event.x
                            lastTouchY = event.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val currentTouchX = event.x
                            val currentTouchY = event.y
                            val dx = currentTouchX - lastTouchX
                            val dy = currentTouchY - lastTouchY

                            mapView.scrollBy(-dx.toInt(), -dy.toInt())

                            lastTouchX = currentTouchX
                            lastTouchY = currentTouchY
                        }
                        MotionEvent.ACTION_UP -> {
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
                    }
                }
                return true
            }
        })


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

    val startPoint = GeoPoint(4.62866, -74.06460);
    override fun onResume() {
        super.onResume()
        mapView.onResume()
        val mapController: IMapController = mapView.controller
        mapController.setZoom(18.0)
        mapController.setCenter(this.startPoint)
        val uiManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        if (uiManager.nightMode == UiModeManager.MODE_NIGHT_YES)
            mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
    }
}
