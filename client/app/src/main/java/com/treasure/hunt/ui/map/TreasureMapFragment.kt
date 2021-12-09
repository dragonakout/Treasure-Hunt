package com.treasure.hunt.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.treasure.hunt.MainActivity
import com.treasure.hunt.R
import com.treasure.hunt.Utils
import com.treasure.hunt.databinding.FragmentMapBinding

class TreasureMapFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private var binding: FragmentMapBinding? = null
    private var mapView: MapView? = null
    private var map: GoogleMap? = null
    private var lastlocationCoords: LatLng? = null
    private var currentPositionMarker: Marker? = null
    private val GEOFENCE_RADIUS_IN_METERS = 100.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding?.root?.findViewById(R.id.map_view)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        InitializeLocationManager()
        lastlocationCoords = getInitialLocation()

        return binding!!.root
    }

    private fun getInitialLocation(): LatLng {
        val lat = Utils.readFloatFromSharedPrefs("lastLocationLatitude", activity)
        val long = Utils.readFloatFromSharedPrefs("lastLocationLongitude", activity)
        return if(lat == null || long == null || (lat == 0f && long == 0f)) {
            LatLng(45.38206, -71.92831) // UdeS
        } else {
            LatLng(lat.toDouble(),long.toDouble())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        mapView = null
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        currentPositionMarker?.remove()
        currentPositionMarker = map?.addMarker(
            MarkerOptions()
                .position(LatLng(lastlocationCoords!!.latitude, lastlocationCoords!!.longitude))
        )
        var coords = getInitialLocation()

        if (lastlocationCoords != null) {
            coords = LatLng(lastlocationCoords!!.latitude, lastlocationCoords!!.longitude)
            map.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
        }
        map.moveCamera(CameraUpdateFactory.newLatLng(coords))

        val lattitude = arguments?.get("lattitude") as Float?
        val longitude = arguments?.get("longitude") as Float?

        if (lattitude != null && longitude != null) {
            coords = LatLng(lattitude.toDouble(), longitude.toDouble())
            map.moveCamera(CameraUpdateFactory.zoomTo(16.0f))
            map.moveCamera(CameraUpdateFactory.newLatLng(coords))
        }
        map.setMinZoomPreference(13.0f)
        map.setMaxZoomPreference(18.0f)
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))
        // If location is not enabled, disable mylocation

        val treasures = (requireActivity() as MainActivity).treasures
        for (i in treasures) {
            val mark = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.raw.img_x_mark))
                .position(LatLng(i.latitude, i.longitude), 75f, 75f)
            map.addGroundOverlay(mark);
        }
    }

    private fun InitializeLocationManager() {
        // If location permission was granted
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && lastlocationCoords == null
        ) {
            val locationManager = getSystemService(
                requireContext(),
                LocationManager::class.java
            ) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000,
                10f,
                this
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        lastlocationCoords = LatLng(location.latitude, location.longitude)
        currentPositionMarker?.remove()
        currentPositionMarker = map?.addMarker(
            MarkerOptions()
                .position(LatLng(lastlocationCoords!!.latitude, lastlocationCoords!!.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.raw.img_maps_marker))
        )
        if (activity != null) {
            Utils.writeToSharedPrefs("lastLocationLatitude", location.latitude.toFloat(), activity)
            Utils.writeToSharedPrefs("lastLocationLongitude", location.longitude.toFloat(), activity)
            checkGeofences()
        }
    }

    private fun checkGeofences() {
        val treasures = (requireActivity() as MainActivity).treasures.toList()
        val locationCoords = LatLng(lastlocationCoords!!.latitude, lastlocationCoords!!.longitude)
        for (treasure in treasures) {
            val treasureCoords = LatLng(treasure.latitude, treasure.longitude)
            if (Utils.distanceCoordsToM(treasureCoords, locationCoords) < GEOFENCE_RADIUS_IN_METERS ) {
                (activity as MainActivity).collectTreasure(treasure)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}