package com.khilman.www.go_send

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.*
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.maps.android.PolyUtil
import com.khilman.www.go_send.model.Order
import com.khilman.www.go_send.network.InitLibrary
import com.khilman.www.go_send.network.model.ResponseRoute
import com.khilman.www.go_send.service.TrackerService
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private fun showMarkerPanel() {
        confirm_address_map_custom_marker.visibility = View.VISIBLE
        btnSeMarker.visibility = View.VISIBLE
    }

    private fun hideMarkerPanel() {
        confirm_address_map_custom_marker.visibility = View.GONE
        btnSeMarker.visibility = View.GONE
    }


    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocation: FusedLocationProviderClient

    private val RC_FROM = 2

    private val RC_TO = 3

    companion object {
        private var currentLatLong: LatLng? = null
    }

    var latLongFrom: LatLng? = null
    var latLongTo: LatLng? = null
    private var totalPrice: Double? = null
    private var travelTime: String? = null
    private var travelDistannce: Double? = null
    private val PERMISSION_REQUEST: Int = 20


    // Marker
    var markerCurrentSelected : Marker ? = null
    var markerPickup : Marker ? = null
    var markerSend : Marker ? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)

        mFusedLocation.lastLocation.addOnSuccessListener(this, object : OnSuccessListener<Location> {
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
        // OnClick
        btnNext.setOnClickListener {
            val order = Order(latLongFrom!!, latLongTo!!, totalPrice!!, travelDistannce!!, travelTime!!)
            startActivity<OrderDetaukActivity>("LAT_FROM" to latLongFrom?.latitude,
                    "LONG_FROM" to latLongFrom?.longitude,
                    "LAT_TO" to latLongTo?.latitude,
                    "LONG_TO" to latLongTo?.longitude,
                    "TRAVEL_TIME" to travelTime,
                    "TRAVEL_PRICE" to totalPrice,
                    "TRAVEL_DISTANCE" to travelDistannce)
        }
        // Check GPS is enabled
        val lm : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //toast("Pease enable location service")
            finish()
        }
        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
       // startTrackerService()
        if (permission == PackageManager.PERMISSION_GRANTED){
            //toast("Diijinkan")
            startTrackerService()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST)
        }

        // Floating marker
        btnSeMarker.onClick {
            if (markerCurrentSelected != null){


//                if (requestCode == RC_FROM) {
//                    latLongFrom = placeLatLang
//                    // Set to text view
//                    tvPickUpFrom?.text = "$placeName - $placeAddres"
//                    if (latLongTo != null) {
//                        // Clear dulu
//                        mMap.clear()
//                        // Baru set marker lagi
//                        addGreenMarker(latLongTo!!, "Send To", true)
//                    }
//                    // Add marker
//                    addBlueMarker(placeLatLang, "Pick Up From", true)
//                } else {
//                    latLongTo = placeLatLang
//                    // Set to text view
//                    tvSendTo?.text = "$placeName - $placeAddres"
//                    if (latLongFrom != null) {
//                        // Clear dulu
//                        mMap.clear()
//                        // Baru set marker lagi
//                        addBlueMarker(latLongFrom!!, "Pick Up From", true)
//
//                    }
//                    // Add marker
//                    addGreenMarker(placeLatLang, "Send To", true)
//
//
//                }

                if (markerCurrentSelected!!.equals(markerPickup)){
                    addBlueMarker(LatLng(mPosition!!.latitude, mPosition!!.longitude), "Pick Up", false)

                } else if (markerCurrentSelected!!.equals(markerSend)){
                    addGreenMarker(LatLng(mPosition!!.latitude, mPosition!!.longitude), "Pick Up", false)
                }
            }
            // Hide panel again
            hideMarkerPanel()
        }

    }

    private fun startTrackerService() {
        //toast("kesisniii")
        startService(Intent(this, TrackerService::class.java))
        //finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST && grantResults.size == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService()
        } else {
            finish()
        }
    }
    private fun setCurrentLocationMarker(location: Location?) {
        // Clear Map First
        if (latLongFrom != null && latLongTo != null) {
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
                val geoCoder = Geocoder(this@MainActivity, Locale.getDefault())

                try {
                    val listAddress: List<Address> = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (null != listAddress && listAddress.size > 0) {
                        val placeAddress = listAddress.get(0).getAddressLine(0)
                        val placeName = listAddress.get(0).featureName
                        Log.d("location me", "${listAddress.get(0).featureName} ${listAddress.get(0).adminArea} ${listAddress.get(0).subLocality} ${listAddress.get(0).locale}")
                        // Set to Pickup text view
                        tvPickUpFrom.text = "${placeName} - ${placeAddress}"
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }


                addBlueMarker(myLocation, "Pick Up Location", true)

                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12f))
                val cu = CameraUpdateFactory.newLatLngZoom(myLocation, 16f)
                // Animate Camera
                mMap.moveCamera(cu)
            }

        }
    }

    private fun addGreenMarker(location: LatLng, title: String, isDragable: Boolean) {
        val width = 50
        val height = 65

        val bitmapdraw = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resources.getDrawable(R.drawable.mini_marker_to, application.theme) as BitmapDrawable
        } else {
            resources.getDrawable(R.drawable.mini_marker_to) as BitmapDrawable
        }
        //val bitmapdraw = BitmapDescriptorFactory.fromResource(R.drawable.mini_marker_to)
        val b = bitmapdraw.bitmap

        markerCurrentSelected = markerSend
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        markerSend = mMap.addMarker(MarkerOptions().position(location)
                .title(title))
        markerSend!!.isDraggable = isDragable
        markerSend!!.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker))

        // Save to Current marker selected variable
        // this will helpfuly soon

    }
    private fun addBlueMarker(location: LatLng, title: String, isDragable: Boolean) {
        val width = 50
        val height = 65

        val bitmapdraw = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resources.getDrawable(R.drawable.mini_marker_from, application.theme) as BitmapDrawable
        } else {
            resources.getDrawable(R.drawable.mini_marker_from) as BitmapDrawable
        }
        //val bitmapdraw = BitmapDescriptorFactory.fromResource(R.drawable.mini_marker_to)
        val b = bitmapdraw.bitmap

        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        markerPickup = mMap.addMarker(MarkerOptions().position(location)
                .title(title))
        markerPickup!!.isDraggable = isDragable
        markerPickup!!.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker))

        // Save to Current marker selected variable
        // this will helpfuly soon
        markerCurrentSelected = markerPickup
    }


    private fun showPlaceAutoComplete(REQUEST_CODE: Int) {
        try {
            // Filter Region
            val typeFilter = AutocompleteFilter.Builder().setCountry("ID").build()
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(this)

            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            // TODO: Handle the error.
        } catch (e: GooglePlayServicesNotAvailableException) {
            // TODO: Handle the error.
        }

    }

    private var mPosition: LatLng? = null
    private var mZoom: Float? = null

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

        mMap.setOnCameraMoveListener(object : GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener {
            override fun onCameraMove() {

            }

            override fun onCameraMoveStarted(p0: Int) {

                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        mMap.setOnCameraIdleListener(object : GoogleMap.OnCameraIdleListener{
            override fun onCameraIdle() {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                // Cleaning all the markers.
                if (mMap != null) {
                    //mMap.clear();
                }

                mPosition = mMap.getCameraPosition().target;
                mZoom = mMap.cameraPosition.zoom

                Log.d("Camera position", "Lat long $mPosition")
                Log.d("Camera position", "ZOOm $mZoom")

            }

        })


        if (markerPickup != null){

        }
        mMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(myMarker: Marker?): Boolean {
                if (myMarker != null){
                    // Show marker pickup panel
                    showMarkerPanel()
                    if (myMarker.equals(markerPickup)){
                        // Remove old pickup marker
                        markerPickup?.remove()
                    } else{
                        // Remove old pikcup marker
                        markerSend?.remove()
                    }

                }

                return true
            }

        })
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
//                mFusedLocation.lastLocation.addOnSuccessListener(this@MainActivity, object : OnSuccessListener<Location>{
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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //
        if (resultCode == RESULT_OK) {
            // If Valid
            val place: Place = PlaceAutocomplete.getPlace(this, data)
            if (place.isDataValid) {
                Log.d("place name", "" + place)
                Log.d("place name", "" + requestCode)
                Log.d("place name", "${place.address} ${place.attributions} ${place.latLng} ${place.id} ${place.rating}")
                // Get place information
                val placeName = place.name
                val placeAddres = place.address
                // get place Coordante
                var placeLatLang = place.latLng
                if (requestCode == RC_FROM) {
                    latLongFrom = placeLatLang
                    // Set to text view
                    tvPickUpFrom?.text = "$placeName - $placeAddres"
                    if (latLongTo != null) {
                        // Clear dulu
                        mMap.clear()
                        // Baru set marker lagi
                        addGreenMarker(latLongTo!!, "Send To", true)
                    }
                    // Add marker
                    addBlueMarker(placeLatLang, "Pick Up From", true)
                } else {
                    latLongTo = placeLatLang
                    // Set to text view
                    tvSendTo?.text = "$placeName - $placeAddres"
                    if (latLongFrom != null) {
                        // Clear dulu
                        mMap.clear()
                        // Baru set marker lagi
                        addBlueMarker(latLongFrom!!, "Pick Up From", true)

                    }
                    // Add marker
                    addGreenMarker(placeLatLang, "Send To", true)


                }

                // Zoom
                val cu = CameraUpdateFactory.newLatLngZoom(placeLatLang, 16f)
                // Animate Camera
                mMap.animateCamera(cu)

                // Zoom beetwen 2 coordinate
                if (latLongFrom != null && latLongTo != null) {

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
                // Display Panel Info
                infoPanel.visibility = View.VISIBLE
                mMap.setPadding(10, 180, 10, 180)
            }

        }
    }

    private fun actionRoute() {
        //Toast.makeText(this@MainActivity, "somo1", Toast.LENGTH_SHORT).show()
        val api = InitLibrary().getInstance()
        val call = api.request_tampil_catatan("${latLongFrom?.latitude},${latLongFrom?.longitude}", "${latLongTo?.latitude},${latLongTo?.longitude}")
        call.enqueue(object : retrofit2.Callback<ResponseRoute> {
            override fun onFailure(call: Call<ResponseRoute>?, t: Throwable?) {
                //Toast.makeText(this@MainActivity, "somo3", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseRoute>?, response: Response<ResponseRoute>?) {
                //Toast.makeText(this@MainActivity, "somo2", Toast.LENGTH_SHORT).show()
                Log.d("rs map", "" + response.toString())
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                if (response?.isSuccessful!!) {
                    // Get Routes Object
                    val routes = response.body()?.routes?.get(0)
                    // Get Price & Distance information
                    val price = "Rp0.0"
                    val distance = routes?.legs?.get(0)?.distance?.text
                    // Jarak Per meter
                    val distanceMeters = routes?.legs?.get(0)?.distance?.value
                    // Ubah ke satuan kilo meter
                    val distanceKm = (distanceMeters!! / 1000)
                    // Tarif permeter
                    val priceMeters = 5400.0

                    // total price
                    //val totalPrice : Double = distanceMeters!! * priceMeters // harga * meter
                    totalPrice = distanceKm * priceMeters // harga * kilo meter
                    travelTime = routes?.legs?.get(0)?.duration?.text
                    travelDistannce = distanceKm.toDouble()
                    // Text View
                    tvPrice.text = CHelper().toRupiahFormat(totalPrice)
                    tvDistance.text = "$distance"

                    Log.d("rs map", "" + response?.body()?.routes?.get(0))
                    Log.d("rs polyline", "" + response?.body()?.routes?.get(0)?.overviewPolyline?.points.toString())

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

}
