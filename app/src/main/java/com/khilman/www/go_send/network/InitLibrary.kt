package com.khilman.www.go_send.network

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit



/**
 * Created by root on 21/01/18.
 */

class InitLibrary {
    // url server
    var SERVER_URL = "https://maps.googleapis.com/maps/api/"


    fun setInit(): Retrofit {
        return Retrofit.Builder().baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }


    fun getInstance(): ApiServices {
        return setInit().create(ApiServices::class.java)
    }
}
