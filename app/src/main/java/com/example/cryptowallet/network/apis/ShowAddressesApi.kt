package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.ShowAddresses
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ShowAddressesApi {
    @GET("v2/accounts/ef804b1f-c74d-5679-bda7-9b2a25863f22/addresses")
    fun getAddress(@Header("Authorization") token:String
    ): Call<ShowAddresses>
}