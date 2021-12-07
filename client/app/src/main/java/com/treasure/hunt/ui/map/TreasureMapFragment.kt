package com.treasure.hunt.ui.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.treasure.hunt.MainActivity
import com.treasure.hunt.R
import com.treasure.hunt.databinding.FragmentMapBinding
import com.treasure.hunt.databinding.FragmentQuestsBinding

class TreasureMapFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentMapBinding? = null
    private var mapView: MapView? = null
    // This property is only valid between onCreateView and
    // onDestroyView.

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
        val coords = LatLng(45.38206, -71.92831) // UdeS
        map.setMinZoomPreference(10.0f)
        map.setMaxZoomPreference(18.0f)
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity as Context, R.raw.map_style))

        map.moveCamera(CameraUpdateFactory.newLatLng(coords))

        val mark = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.raw.img_x_mark))
            .position(LatLng(45.38745, -71.899185), 50f, 50f)
        map.addGroundOverlay(mark);

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