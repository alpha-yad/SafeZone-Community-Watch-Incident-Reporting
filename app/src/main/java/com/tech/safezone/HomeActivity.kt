package com.tech.safezone

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.tech.safezone.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class HomeActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        // Get the map fragment and notify when it's ready
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    // This method is triggered when the map is ready to be used
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        // Add a marker at a specific location (e.g., New York City)
        val newYork = LatLng(40.7128, -74.0060)
        mMap!!.addMarker(MarkerOptions().position(newYork).title("Marker in New York"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(newYork, 10f)) // Zoom level
    }
}
