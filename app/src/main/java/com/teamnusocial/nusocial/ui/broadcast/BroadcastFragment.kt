package com.teamnusocial.nusocial.ui.broadcast

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.ui.auth.SignInActivity
import com.teamnusocial.nusocial.ui.community.CommunityFragment
import kotlinx.android.synthetic.main.content_broadcast_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_broadcast.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    private lateinit var circle: Circle
    private lateinit var circleOptions: CircleOptions

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

    private fun moveToCommunityFragment() {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment, CommunityFragment())
            .commit()
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
                        broadcastArrow.background =
                            requireContext().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24)
                        Log.d("BROADCAST", "Bottom Sheet Expanded")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        broadcastArrow.background =
                            requireContext().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24)
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

        CoroutineScope(Dispatchers.IO).launch {
            broadcastViewModel.updateCurrentLocation()
        }
        broadcastViewModel.currentUserLocation.observe(requireActivity(), Observer {
            if (it.latitude != 0.0 && it.longitude != 0.0) {
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.broadcastMap) as SupportMapFragment

                mapFragment.getMapAsync(this)
                if (this::marker.isInitialized) {
                    marker.remove()
                }
                markerOptions = MarkerOptions()
                markerOptions.position(it)
                cameraPosition = CameraPosition.Builder()
                    .target(it)
                    .zoom(17f)
                    .build()
                updateRadiusOnMap(it, broadcastViewModel.broadcastRadius.value!!)
            }
        })

        updateLocation()

        broadcastSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                broadcastViewModel.updateRadius(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        broadcastViewModel.broadcastRadius.observe(requireActivity(), Observer {
            broadCastRadiusTextView.text =
                if (it > 999) ((it / 1000).toString() + " km") else ("$it m")
            updateRadiusOnMap(broadcastViewModel.currentUserLocation.value!!, it)
        })
    }

    private fun updateRadiusOnMap(center: LatLng, radius: Int) {
        circleOptions = CircleOptions()
            .center(center)
            .radius(radius.toDouble())
            .strokeWidth(1.0f)
            .strokeColor(ContextCompat.getColor(context!!, R.color.orange))
            .fillColor(ContextCompat.getColor(context!!, R.color.blue))
        if (this::circle.isInitialized) {
            circle.remove()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap!!
        marker = googleMap.addMarker(markerOptions)
        circle = googleMap.addCircle(circleOptions)
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun updateLocation() {

        val lm: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            moveToCommunityFragment()
        }

        val permission: Int = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startLocationService()
        } else {
            Dexter.withContext(requireContext()).withPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Log.d("BROADCAST", "Location permission given")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    startActivity(Intent(requireContext(), SignInActivity::class.java))
                }

            }).check()
        }

    }

    private fun startLocationService() {
        Log.d("BROADCAST", "Starting service")
        requireActivity().startService(
            Intent(
                requireActivity(),
                LocationTrackingService::class.java
            )
        )
    }
}