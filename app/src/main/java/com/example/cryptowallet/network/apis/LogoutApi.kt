package com.example.cryptowallet.network.apis

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

//https://api.coinbase.com/oauth/revoke

interface LogoutApi {
    @GET("oauth/revoke")
    fun logout(@Header("Authorization") token:String
    ):Call<Any>
}