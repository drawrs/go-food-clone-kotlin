package com.khilman.www.go_send

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
;import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.maps.android.PolyUtil
import com.khilman.www.go_send.network.InitLibrary
import com.khilman.www.go_send.network.model.ResponseRoute
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    override fun onLocationChanged(location: Location?) {
        //
        Log.d("Location", location.toString())
        Log.d("Location", "${location?.latitude} ${location?.longitude}")
    }

    override fun onConnected(p0: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionSuspended(p0: Int) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocation: FusedLocationProviderClient

    var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1

    private val RC_FROM = 2

    private val RC_TO = 3

    private  var currentLatLong: LatLng ? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)

        mFusedLocation.lastLocation.addOnSuccessListener(this, object : OnSuccessListener<Location>{
            override fun onSuccess(location: Location?) {
                // Set Current Location Marker
                setCurrentLocationMarker(location)
            }

        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Click handle
        tvPickUpFrom.setOnClickListener {
            // Pickup place
            showPlaceAutoComplete(RC_FROM)
        }

        // Click handle
        tvSendTo.setOnClickListener {
            // Pickup place
            showPlaceAutoComplete(RC_TO)
        }
    }

    private fun setCurrentLocationMarker(location: Location?) {
        // Clear Map First
        if (latLongFrom != null && latLongTo != null){
            mMap.clear()
            Toast.makeText(this, "aha", Toast.LENGTH_SHORT).show()
        } else {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
                Log.d("Location nowyee", location.toString())
                Log.d("Location nowvyee", location.altitude.toString())

                latLongFrom = LatLng(location.latitude, location.longitude)
                val myLocation = LatLng(location.latitude, location.longitude)
                val geoCoder = Geocoder(this@MapsActivity, Locale.getDefault())

                try {
                    val listAddress : List<Address> = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    if(null != listAddress && listAddress.size > 0){
                        val placeAddress= listAddress.get(0).getAddressLine(0)
                        val placeName = listAddress.get(0).featureName
                        Log.d("location me", "${listAddress.get(0).featureName} ${listAddress.get(0).adminArea} ${listAddress.get(0).subLocality} ${listAddress.get(0).locale}")
                        // Set to Pickup text view
                        tvPickUpFrom.text = "${placeName} - ${placeAddress}"
                    }

                }catch (e: IOException){
                    e.printStackTrace()
                }

                mMap.addMarker(MarkerOptions().position(myLocation).title("Pick Up Location"))
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12f))
                val cu = CameraUpdateFactory.newLatLngZoom(myLocation, 16f)
                // Animate Camera
                mMap.animateCamera(cu)
        }

        }
    }


    private fun showPlaceAutoComplete(REQUEST_CODE: Int) {
        try {
            // Filter Region
            val typeFilter = AutocompleteFilter.Builder().setCountry("ID").build()
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(this)

            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            // TODO: Handle the error.
        } catch (e: GooglePlayServicesNotAvailableException) {
            // TODO: Handle the error.
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        mFusedLocation.lastLocation.addOnSuccessListener(this, object : OnSuccessListener<Location>{
//            override fun onSuccess(location: Location?) {
//                // Got last known location. In some rare situations this can be null.
//                if (location != null) {
//                    // Logic to handle location object
//                    Log.d("Location now yee", location.toString())
//                    Log.d("Location now huu", location.altitude.toString())
//
//                    currentLatLong = LatLng(location.longitude, location.longitude)
//
//                    mMap.addMarker(MarkerOptions().position(currentLatLong!!).title("Pick Up Location"))
//
//                }
//            }
//
//        })
        // Add a marker in Sydney and move the camera
        //val myLocation = currentLatLong
        mMap.setPadding(10, 180, 10, 10)
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.getUiSettings().setCompassEnabled(true)
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.getUiSettings().setZoomControlsEnabled(true)


        // If is my location button Clicked
//        mMap.setOnMyLocationClickListener(object: GoogleMap.OnMyLocationClickListener {
//            override fun onMyLocationClick(location: Location) {
//                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                setCurrentLocationMarker(location)
//            }
//        })

//        mMap.setOnMyLocationButtonClickListener(object : GoogleMap.OnMyLocationButtonClickListener{
//            override fun onMyLocationButtonClick(): Boolean {
//                //TODO("not implemented") //To change body of created functions use File | SettingsmF | File Templates.
//
//                mFusedLocation.lastLocation.addOnSuccessListener(this@MapsActivity, object : OnSuccessListener<Location>{
//                    override fun onSuccess(location: Location?) {
//                        // Set Current Location Marker
//
//                        setCurrentLocationMarker(location)
//                    }
//
//                })
//                return true
//            }
//
//        })
//        val autocompleteFragment = fragmentManager.findFragmentById(R.id.map) as PlaceAutocompleteFragment
//        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place?) {
//                //
//                Log.i("Maps Ac", "Place: " + place?.getName());
//
//            }
//
//            override fun onError(status: Status?) {
//                Log.i("Maps Ac", "An error occurred: " + status);
//
//            }
//
//        })
    }

    var latLongFrom: LatLng? = null
    var latLongTo: LatLng? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //
        if (resultCode == RESULT_OK){
            // If Valid
            val place : Place = PlaceAutocomplete.getPlace(this, data)
            if (place.isDataValid){
                Log.d("place name", "" + place)
                Log.d("place name", "" + requestCode)
                Log.d("place name", "${place.address} ${place.attributions} ${place.latLng} ${place.id} ${place.rating}" )
                // Get place information
                val placeName = place.name
                val placeAddres = place.address
                // get place Coordante
                var placeLatLang = place.latLng
                if (requestCode == RC_FROM){
                    latLongFrom = placeLatLang
                    // Set to text view
                    tvPickUpFrom?.text = "$placeName - $placeAddres"
                    if (latLongTo != null){
                        // Clear dulu
                        mMap.clear()
                        // Baru set marker lagi
                        mMap.addMarker(MarkerOptions().position(latLongTo!!).title("Pick Up From"))
                    }
                    // Add marker
                    mMap.addMarker(MarkerOptions().position(placeLatLang).title("Send To"))
                } else {
                    latLongTo = placeLatLang
                    // Set to text view
                    tvSendTo?.text = "$placeName - $placeAddres"
                    if (latLongFrom != null){
                        // Clear dulu
                        mMap.clear()
                        // Baru set marker lagi
                        mMap.addMarker(MarkerOptions().position(latLongFrom!!).title("Pick Up From"))
                    }
                    // Add marker
                    mMap.addMarker(MarkerOptions().position(placeLatLang).title("Send To"))

                }

                // Zoom
                val cu = CameraUpdateFactory.newLatLngZoom(placeLatLang, 16f)
                // Animate Camera
                mMap.animateCamera(cu)

                // Zoom beetwen 2 coordinate
                if (latLongFrom != null && latLongTo != null){

                    // Polyline Routing
                    actionRoute()

                    val latLongBuilder = LatLngBounds.Builder()
                    latLongBuilder.include(latLongFrom)
                    latLongBuilder.include(latLongTo)

                    // Bounds Coordinata
                    val bounds = latLongBuilder.build()

                    val width = resources.displayMetrics.widthPixels
                    val height = resources.displayMetrics.heightPixels
                    val paddingMap = (width * 0.2).toInt()//jarak dari
                    val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, paddingMap)
                    mMap.animateCamera(cu)

                }
//                val latLongBuilder = LatLngBounds.Builder()
//
//                latLongBuilder.include(LatLng(java.lang.Double.parseDouble(data.getBookingFromLat()), java.lang.Double.parseDouble(data.getBookingFromLng())))
//                latLongBuilder.include(LatLng(java.lang.Double.parseDouble(data.getBookingTujuanLat()), java.lang.Double.parseDouble(data.getBookingTujuanLng())))
//
//                //dapatkan koordinat di tengah-tengah
//                val bounds = latLongBuilder.build()
//
//                val width = resources.displayMetrics.widthPixels
//                val height = resources.displayMetrics.heightPixels
//                val paddingMap = (width * 0.1).toInt()//jarak dari sisi map 30%
//
//                val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, paddingMap)
//                mMap.animateCamera(cu)

                // Display Panel Info
                infoPanel.visibility = View.VISIBLE
                mMap.setPadding(10, 180, 10, 180)
            }

        }
    }

    private fun actionRoute() {
        Toast.makeText(this@MapsActivity, "somo1", Toast.LENGTH_SHORT).show()
        val api = InitLibrary().getInstance()
        val call = api.request_tampil_catatan("${latLongFrom?.latitude},${latLongFrom?.longitude}", "${latLongTo?.latitude},${latLongTo?.longitude}")
        call.enqueue(object : retrofit2.Callback<ResponseRoute> {
            override fun onFailure(call: Call<ResponseRoute>?, t: Throwable?) {
                Toast.makeText(this@MapsActivity, "somo3", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseRoute>?, response: Response<ResponseRoute>?) {
                Toast.makeText(this@MapsActivity, "somo2", Toast.LENGTH_SHORT).show()
                Log.d("rs map", "" + response.toString())
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                if (response?.isSuccessful!!){
                    // Get Routes Object
                    val routes = response.body()?.routes?.get(0)
                    // Get Price & Distance information
                    val price = "Rp0.0"
                    val distance = routes?.legs?.get(0)?.distance?.text
                    // Jarak Per meter
                    val distanceMeters = routes?.legs?.get(0)?.distance?.value
                    // Ubah ke satuan kilo meter
                    val distanceKm  = (distanceMeters!!/1000)
                    // Tarif permeter
                    val priceMeters = 5400.0

                    // total price
                    //val totalPrice : Double = distanceMeters!! * priceMeters // harga * meter
                    val totalPrice : Double = distanceKm * priceMeters // harga * kilo meter

                    // Text View
                    tvPrice.text = CHelper().toRupiahFormat(totalPrice)
                    tvDistance.text = "$distance"

                    Log.d("rs map", "" + response?.body()?.routes?.get(0))
                    Log.d("rs polyline", "" + response?.body()?.routes?.get(0)?.overviewPolyline?.points.toString() )

                    // get Polyline
                    val polylinePoints = routes?.overviewPolyline?.points

                    // Log
                    Log.d("rs map", "" + routes?.overviewPolyline.toString())

                    // Let's decode polyline points
                    val decodedPath = PolyUtil.decode(polylinePoints)
                    // Set decoded path into Map
                    mMap.addPolyline(PolylineOptions().addAll(decodedPath)
                                                        .width(8f)
                                                        .color(Color.argb(255, 56, 167, 252))
                            .geodesic(true))

                    //mMap.addPolyline(PolylineOptions().add(latLongFrom, latLongTo).width(5f).color(Color.RED))



                }
            }

        })
    }

    // Biarkan
    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(p0: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(p0: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
