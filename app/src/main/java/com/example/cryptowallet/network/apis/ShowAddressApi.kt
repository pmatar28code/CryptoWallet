package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.NAddress
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ShowAddressApi {
    @GET("v2/accounts/68c41609-6b0f-5209-a655-e9a81ddd91d2/addresses")
    fun getAddress(@Header("Authorization") token:String
    ): Call<NAddress>
}