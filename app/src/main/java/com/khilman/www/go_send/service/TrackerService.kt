package com.khilman.www.go_send.service

import android.Manifest
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

/**
 * Created by root on 03/02/18.
 */
public class TrackerService : Service() {
    val TAG = "trackerService"
    private var currentLatLng : LatLng ? = null
    private var currentLat: Double ? = null
    private var currentLng: Double ? = null
    override fun onBind(p0: Intent?): IBinder {
        Log.d(TAG, "Location updated")

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return null!!
    }

    override fun onCreate() {
        super.onCreate()
        requestLocationUpdates()
    }



    private fun requestLocationUpdates() {
        Log.d(TAG, "Location updated")
        val request = LocationRequest()
        request.interval = 10000
        request.fastestInterval = 5000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // Get current location
        val client : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED){
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    // Get location
                    val location = locationResult?.lastLocation
                    if (location != null){
                        val resultLatLng = LatLng(location.latitude, location.longitude)
                        if (currentLatLng != resultLatLng){
                            currentLat = location.latitude
                            currentLng = location.longitude
                            currentLatLng = LatLng(currentLat!!, currentLng!!)

                            Log.d(TAG, "Location updated" + location)
                            Log.d(TAG, "Lat long $currentLat $currentLng" )
                        }

                    }
                }
            }, null)
        }
    }

}