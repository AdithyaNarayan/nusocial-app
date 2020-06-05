package com.teamnusocial.nusocial.ui.broadcast

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.teamnusocial.nusocial.R
import kotlinx.android.synthetic.main.content_broadcast_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_broadcast.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class BroadcastFragment : Fragment(), KodeinAware, OnMapReadyCallback {

    override val kodein by closestKodein()
    private val factory by instance<BroadcastViewModelFactory>()
    private lateinit var broadcastViewModel: BroadcastViewModel

    private lateinit var sheetBehavior: BottomSheetBehavior<RelativeLayout>

    private lateinit var googleMap: GoogleMap
    private lateinit var marker: Marker
    private lateinit var markerOptions: MarkerOptions
    private lateinit var cameraPosition: CameraPosition

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        broadcastViewModel =
            ViewModelProvider(this, factory).get(BroadcastViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_broadcast, container, false)
//        val textView: TextView = root.findViewById(R.id.text_broadcast)
//        broadcastViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        CoroutineScope(Dispatchers.IO).launch {
            Log.d("USER", broadcastViewModel.getUsers().size.toString())
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sheetBehavior =
            BottomSheetBehavior.from(broadcastBottomSheetLayout)

        sheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d("BROADCAST", "Bottom Sheet Expanded")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d("BROADCAST", "Bottom Sheet Collapsed")
                    }
                }
            }

            override fun onSlide(view: View, p1: Float) {
            }
        })

        broadcastRootView.setOnClickListener {
            if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.broadcastMap) as SupportMapFragment

        mapFragment.getMapAsync(this)

        markerOptions = MarkerOptions()
        val latLng = LatLng(1.3521, 103.8198)
        markerOptions.position(latLng)
        cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(17f)
            .build()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap!!
        marker = googleMap.addMarker(markerOptions)
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}