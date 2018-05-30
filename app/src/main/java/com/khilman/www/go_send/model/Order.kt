package com.khilman.www.go_send.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

/**
 * Created by root on 24/01/18.
 */
@SuppressLint("ParcelCreator")
data class Order (
        var latLongFrom: LatLng,
        var latLongTo: LatLng,
        var price: Double,
        var distance: Double,
        var time: String
) : Parcelable {
    override fun writeToParcel(p0: Parcel?, p1: Int) {

    }

    override fun describeContents(): Int {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return 0
    }
}