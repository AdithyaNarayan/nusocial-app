package com.teamnusocial.nusocial.ui.broadcast

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.location.LocationManager
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.ui.auth.SignInActivity
import com.teamnusocial.nusocial.ui.community.CommunityFragment
import kotlinx.android.synthetic.main.content_broadcast_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_broadcast.*
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.net.URL
import java.time.LocalDate
import java.util.*


class BroadcastFragment : Fragment(), KodeinAware, OnMapReadyCallback {

    override val kodein by closestKodein()
    private val factory by instance<BroadcastViewModelFactory>()
    private lateinit var broadcastViewModel: BroadcastViewModel

    private lateinit var sheetBehavior: BottomSheetBehavior<RelativeLayout>

    private lateinit var googleMap: GoogleMap
    private lateinit var marker: Marker
    private lateinit var markerOptions: MarkerOptions

    private lateinit var userMarkers: MutableList<Marker>
    private var userMarkerOptions: MutableList<MarkerOptions> = mutableListOf()
    private lateinit var cameraPosition: CameraPosition
    private lateinit var circle: Circle
    private lateinit var circleOptions: CircleOptions
    private lateinit var locationPinBitmap: BitmapDescriptor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        broadcastViewModel =
            ViewModelProvider(this, factory).get(BroadcastViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_broadcast, container, false)
        /*CoroutineScope(Dispatchers.IO).launch {
            val bitmap = BitmapFactory.decodeStream(
                URL(broadcastViewModel.getCurrentUserAsUser().profilePicturePath)
                    .openConnection()
                    .getInputStream()
            )
            withContext(Dispatchers.Main) {
                broadcastViewModel.profileImg.value = bitmap
            }
        }*/
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("USER", broadcastViewModel.getUsers().size.toString())
        }
        broadcastViewModel.profileImg.observe(requireActivity(), Observer {
            if (it != null) {
                locationPinBitmap = getLocationPinImage(it)
            }
        })
        broadcastViewModel.closestNeighboursList.observe(requireActivity(), Observer {
            if (it.size > 0) {
                updateUsersOnMap(it)
                if (isAdded) {
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.broadcastMap) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
        })
        locationPinBitmap = BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeResource(
                requireContext().resources,
                R.drawable.location_pin
            )
        )
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
            } else sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
        }
        broadcastArrow.setOnClickListener {
            if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
        }

        CoroutineScope(Dispatchers.IO).launch {
            broadcastViewModel.updateCurrentLocation()
        }
        broadcastViewModel.currentUserLocation.observe(requireActivity(), Observer {
            if (it.latitude != 0.0 && it.longitude != 0.0) {
                if (isAdded) {
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.broadcastMap) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
                updateMarkerOnMap(it)
                updateRadiusOnMap(it, broadcastViewModel.broadcastRadius.value!!)
            }
        })

        updateLocationInBackend()

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
            if (isAdded) {
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.broadcastMap) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
            updateMarkerOnMap(broadcastViewModel.currentUserLocation.value!!)
            updateRadiusOnMap(broadcastViewModel.currentUserLocation.value!!, it)
        })

        sendBroadcastButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(
                    "BROADCAST",
                    broadcastViewModel.getCurrentUserAsUser().lastBroadcasted.toDate().toString()
                )
                Log.d("BROADCAST", Date().toString())

                if (isSameDay(
                        broadcastViewModel.getCurrentUserAsUser().lastBroadcasted.toDate(),
                        Date()
                    )
                ) {
                    Snackbar.make(
                        broadcastRootView,
                        "Broadcast limit has been reached for the day",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        broadcastViewModel.sendBroadcast(broadcastMessageText.text.toString())
                        Snackbar.make(
                            broadcastRootView,
                            "Broadcast Message has been radiated successfully!",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }
    }

    private fun updateRadiusOnMap(center: LatLng, radius: Int) {
        circleOptions = CircleOptions()
            .center(center)
            .radius(radius.toDouble())
            .strokeWidth(1.0f)
            .strokeColor(ContextCompat.getColor(requireContext(), R.color.orange))
            .fillColor(ContextCompat.getColor(requireContext(), R.color.blue_transparent))
    }

    private fun updateMarkerOnMap(location: LatLng) {
        markerOptions = MarkerOptions()
            .position(location)
            .icon(locationPinBitmap)
        cameraPosition = CameraPosition.Builder()
            .target(location)
            .zoom(16f)
            .build()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap!!
        if (this::circle.isInitialized) {
            circle.remove()
        }
        if (this::marker.isInitialized) {
            marker.remove()
        }
        if (this::userMarkers.isInitialized) {
            userMarkers.forEach {
                it.remove()
            }
        }

        marker = googleMap.addMarker(markerOptions)
        userMarkers = mutableListOf()
        userMarkerOptions.forEach {
            userMarkers.add(googleMap.addMarker(it))
        }
        circle = googleMap.addCircle(circleOptions)
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun updateLocationInBackend() {
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

    private fun getLocationPinImage(profileBitmap: Bitmap): BitmapDescriptor {
        val locationPinBackground =
            BitmapFactory.decodeResource(requireContext().resources, R.drawable.location_pin)

        // return BitmapDescriptorFactory.fromBitmap(overlay(locationPinBackground, profileBitmap))
        return BitmapDescriptorFactory.fromBitmap(locationPinBackground)
    }

    private fun overlay(firstBmp: Bitmap, secondBmp: Bitmap): Bitmap {
        val bmOverlay = Bitmap.createBitmap(firstBmp.width, firstBmp.height, firstBmp.config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(firstBmp, Matrix(), null)
        canvas.drawBitmap(secondBmp, 0.0f, 0.0f, null)
        return bmOverlay
    }

    private fun updateUsersOnMap(users: List<User>) {
        users.forEach {
            userMarkerOptions.add(
                MarkerOptions()
                    .position(it.location.getAsLatLng())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_pin))
            )
        }
    }

    private fun isSameDay(firstDate: Date, secondDate: Date): Boolean {
        return firstDate.year == secondDate.year && firstDate.month == secondDate.month && firstDate.date == secondDate.date
    }

}