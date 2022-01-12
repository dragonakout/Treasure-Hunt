package com.dragonsko.treasurehunt.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.dragonsko.treasurehunt.MainActivity
import com.dragonsko.treasurehunt.R
import com.dragonsko.treasurehunt.Utils
import com.dragonsko.treasurehunt.databinding.FragmentMapBinding
import com.dragonsko.treasurehunt.service.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*

class TreasureMapFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentMapBinding? = null
    private var mapView: MapView? = null
    private var map: GoogleMap? = null
    private var lastlocationCoords: LatLng? = null
    private var currentPositionMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding?.root?.findViewById(R.id.map_view)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        lastlocationCoords = getInitialLocation()
        (activity as MainActivity).service?.mapNotify = ::updateMapMarkerAndGeofences

        return binding!!.root
    }

    private fun getInitialLocation(): LatLng {
        val lat = (activity as MainActivity).last_position?.latitude
        val long = (activity as MainActivity).last_position?.longitude
        return if(lat == null || long == null) {
            LatLng(45.38206, -71.92831) // UdeS
        } else {
            (activity as MainActivity).last_position!!
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
        currentPositionMarker = map.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.raw.img_maps_marker))
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

        val treasures = (requireActivity() as MainActivity).quests
        for (i in treasures!!) {
            val mark = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.raw.img_x_mark))
                .position(LatLng(i.latitude, i.longitude), 75f, 75f)
            map.addGroundOverlay(mark);
            mark.visible(false)
        }
    }

    fun updateMapMarkerAndGeofences(location : LatLng) {
        lastlocationCoords = LatLng(location.latitude, location.longitude)
        currentPositionMarker?.remove()
        currentPositionMarker = map?.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.raw.img_maps_marker))
                .position(LatLng(lastlocationCoords!!.latitude, lastlocationCoords!!.longitude))
        )
        if(arguments?.get("lattitude") == null || arguments?.get("longitude") == null ) {
            map?.moveCamera(CameraUpdateFactory.newLatLng(location))
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
        (activity as MainActivity).service?.mapNotify = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}