package com.khilman.www.go_send

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.khilman.www.go_send.model.Order
import kotlinx.android.synthetic.main.activity_order_detauk.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

class OrderDetaukActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detauk)
        
        load(this)
    }

    private fun load(context: Context) {
        val latLongFrom = LatLng(intent.getDoubleExtra("LAT_FROM", 0.0), intent.getDoubleExtra("LONG_FROM", 0.0))
        val latLongTo = LatLng(intent.getDoubleExtra("LAT_TO", 0.0), intent.getDoubleExtra("LONG_TO", 0.0))
        val travelTime = intent.getStringExtra("TRAVEL_TIME")
        val travelDistance = intent.getDoubleExtra("TRAVEL_DISTANCE", 0.0)
        val travelPrice = intent.getDoubleExtra("TRAVEL_PRICE", 0.0)

        //Toast.makeText(this, "$latLongFrom $travelTime $travelPrice", Toast.LENGTH_LONG).show()

        tvTravelPrice.text = "${CHelper().toRupiahFormat(travelPrice)}"

        btnOrder.onClick {
            startActivity<FindDriverActivity>()
        }
    }
}
