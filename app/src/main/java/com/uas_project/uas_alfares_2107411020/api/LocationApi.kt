package com.uas_project.uas_alfares_2107411020.api

import com.uas_project.uas_alfares_2107411020.model.Locations
import retrofit2.Call
import retrofit2.http.GET

interface LocationApi {

    @GET("locations")
    fun getLocations(): Call<Locations>
}