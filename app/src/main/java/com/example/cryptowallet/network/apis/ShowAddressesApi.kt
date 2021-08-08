package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.ShowAddresses
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ShowAddressesApi {
    @GET("v2/accounts/0b125b64-619e-5cf8-a719-963534b52bca/addresses")
    fun getAddress(@Header("Authorization") token:String
    ): Call<ShowAddresses>
}