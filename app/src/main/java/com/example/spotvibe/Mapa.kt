package com.example.spotvibe

import android.app.UiModeManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.spotvibe.databinding.ActivityMapaBinding
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import java.util.ArrayList

class Mapa : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView
    private lateinit var binding: ActivityMapaBinding
    private var longPressedMarker: Marker? = null
    private var tappedMarker: Marker? = null
    lateinit var roadManager: RoadManager
    private var roadOverlay: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_mapa)
        map = findViewById<MapView>(R.id.osmMap)
        map.setTileSource(TileSourceFactory.MAPNIK)
        roadManager = OSRMRoadManager(this, "ANDROID")
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val bundle = intent.getBundleExtra("informacionLugar")

            val nombre = bundle?.getString("nombre")
            val autor = bundle?.getString("autor")
            val foto = bundle?.getString("foto")
            val distanciaTexto = bundle?.getString("distanciaTexto")
            val latitud = bundle?.getDouble("latitud")?: 0.0
            val longitud = bundle?.getDouble("longitud")?: 0.0

        val markerPoint1 = GeoPoint(latitud, longitud)
        val marker1 = Marker(map)
        marker1.title = nombre
        marker1.subDescription = autor
        val myIcon1 = resources.getDrawable(R.drawable.sharp_add_location_24, this.theme)
        marker1.icon = myIcon1
        marker1.position = markerPoint1
        marker1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        map.overlays.add(marker1)
        map.overlays.add(createOverlayEvents(markerPoint1))
    }
    val startPoint = GeoPoint(4.62866, -74.06460);
    override fun onResume() {
        super.onResume()
        map.onResume()
        val mapController: IMapController = map.controller
        mapController.setZoom(18.0)
        mapController.setCenter(this.startPoint)
        val uiManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        if(uiManager.nightMode == UiModeManager.MODE_NIGHT_YES)
            map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }
    private fun createOverlayEvents(markerPoint1: GeoPoint): MapEventsOverlay {
        val overlayEventos = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                tapOnOnMap(p, markerPoint1)
                return true
            }
            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        })
        return overlayEventos
    }

    private fun tapOnOnMap(p: GeoPoint, markerPoint1: GeoPoint) {
        tappedMarker?.let { map.overlays.remove(it) }
        tappedMarker = createMarker(p, "Posicion actual", null, R.drawable.sharp_add_location_24, markerPoint1)
        tappedMarker?.let { map.overlays.add(it) }
    }
    private fun createMarker(
        p: GeoPoint,
        title: String?,
        desc: String?,
        iconID: Int,
        markerPoint1: GeoPoint
    ): Marker? {
        var marker: Marker? = null
        if (map != null) {
            marker = Marker(map)
            title?.let { marker.title = it }
            desc?.let { marker.subDescription = it }
            if (iconID != 0) {
                val myIcon = resources.getDrawable(iconID, this.theme)
                marker.icon = myIcon
            }
            marker.position = p
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        drawRoute(p, markerPoint1)
        return marker
    }

    private fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
        val road = roadManager.getRoad(routePoints)
        Log.i("OSM_acticity", "Route length: ${road.mLength} klm")
        Log.i("OSM_acticity", "Duration: ${road.mDuration / 60} min")
        if (map != null) {
            roadOverlay?.let { map.overlays.remove(it) }
            roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay?.outlinePaint?.color = Color.RED
            roadOverlay?.outlinePaint?.strokeWidth = 10f
            map.overlays.add(roadOverlay)
        }
    }
}