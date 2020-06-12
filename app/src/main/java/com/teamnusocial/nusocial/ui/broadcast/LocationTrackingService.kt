package com.teamnusocial.nusocial.ui.broadcast

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.knn.Classifier
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.util.*

class LocationTrackingService() : Service() {
    private val repository: UserRepository = UserRepository(FirestoreUtils())

    companion object {
        private val TAG = LocationTrackingService::class.java.simpleName
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BROADCAST", "Entering Service")
        buildNotification()
        requestLocationUpdates()
    }

    private fun buildNotification() {
        val stop = "Stop Location Tracking"
        registerReceiver(stopReceiver, IntentFilter(stop))
        val broadcastIntent = PendingIntent.getBroadcast(
            this, 0, Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT
        )
        val channelID = "com.teamnusocial.nusocial"
        val channelName = "My Background Service"
        val chan = NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
        startForeground(
            1, Notification.Builder(this, channelID)
                .setContentTitle("NUSocial")
                .setContentText("NUSocial is using your location for the broadcast feature")
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_location_notif)
                .build()
        )
    }

    private var stopReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            unregisterReceiver(this)
            stopSelf()
        }
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest()
        request.interval = 10000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        val permission: Int = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient
                .requestLocationUpdates(request, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {

                        val decimalFormat = DecimalFormat("##.##")
                        val cluster =
                            decimalFormat.format(locationResult.lastLocation!!.latitude) + decimalFormat.format(
                                locationResult.lastLocation!!.longitude
                            )
                        runBlocking {
                            repository.updateCurrentLocation(
                                locationResult.lastLocation!!,
                                cluster
                            )
                        }
                    }
                }, null)
        }
    }

}