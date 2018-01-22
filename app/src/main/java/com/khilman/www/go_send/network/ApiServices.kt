package com.khilman.www.go_send.network

import com.khilman.www.go_send.network.model.ResponseRoute
import com.khilman.www.go_send.network.response.ResponseDirection
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by root on 21/01/18.
 */
interface ApiServices {

    @GET("directions/json")
    fun request_tampil_catatan(
            //?origin=Toronto&destination=Montreal&avoid=tolls&mode=bicycling&key=AIzaSyCFqXa0x5_ScgmTPrrJwNU4QoseYLsBUj0
            @Query("origin") origin : String,
            @Query("destination") destination : String,
            @Query("mode") mode : String ? = "driving",
            @Query("avoid") avoid : String ? = "tolls",
            @Query("key") key : String ? = "AIzaSyCFqXa0x5_ScgmTPrrJwNU4QoseYLsBUj0"
    ): Call<ResponseRoute>
}