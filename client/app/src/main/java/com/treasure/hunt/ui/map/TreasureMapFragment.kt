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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.treasure.hunt.MainActivity
import com.treasure.hunt.R
import com.treasure.hunt.databinding.FragmentMapBinding

class TreasureMapFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private var binding: FragmentMapBinding? = null
    private var mapView: MapView? = null
    private var map: GoogleMap? = null
    private var lastlocation: Location? = null
    private var currentPositionMarker: Marker? = null
    private val fusedLocationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding?.root?.findViewById(R.id.map_view)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        mapView = null
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        updateLocation()
        var coords = LatLng(45.38206, -71.92831) // UdeS
        if (lastlocation != null) {
            coords = LatLng(lastlocation!!.latitude, lastlocation!!.longitude)
            map.moveCamera(CameraUpdateFactory.zoomTo(14.0f))
        }
        map.moveCamera(CameraUpdateFactory.newLatLng(coords))

        val lattitude = arguments?.get("lattitude") as Float?
        val longitude = arguments?.get("longitude") as Float?

        if (lattitude != null && longitude != null){
            coords = LatLng(lattitude.toDouble(), longitude.toDouble())
            map.moveCamera(CameraUpdateFactory.zoomTo(16.0f))
            map.moveCamera(CameraUpdateFactory.newLatLng(coords))
        }
        CameraUpdateFactory.zoomIn()
        map.setMinZoomPreference(13.0f)
        map.setMaxZoomPreference(18.0f)
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))
        // If location is not enabled, disable mylocation

        val treasures = (requireActivity() as MainActivity).treasures
        for( i in treasures) {
            val mark = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.raw.img_x_mark))
                .position(LatLng(i.lattitude.toDouble(), i.longitude.toDouble()), 75f, 75f)
            map.addGroundOverlay(mark);
        }
    }

    private fun updateLocation() {
        // If location permission was granted
        if (!(ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)) {

            val locationManager = getSystemService(requireContext(), LocationManager::class.java) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10f, this)

            }
    }

    override fun onLocationChanged(location: Location) {
        lastlocation = location
        currentPositionMarker?.remove()
        currentPositionMarker  = map?.addMarker(
            MarkerOptions()
                .position(LatLng(lastlocation!!.latitude, lastlocation!!.longitude))
        )
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