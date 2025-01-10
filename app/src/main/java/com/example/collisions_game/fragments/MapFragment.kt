package com.example.collisions_game.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.collisions_game.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        val defaultLocation = LatLng(0.0, 0.0)
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2f))
    }

    fun zoomToLocation(latitude: Double, longitude: Double) {
        googleMap?.let { map ->
            val location = LatLng(latitude, longitude)
            map.addMarker(MarkerOptions().position(location))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }
}