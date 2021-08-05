package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.UserData
import retrofit2.Call
import retrofit2.http.*

interface CoinBaseClientApiCalls {
    @GET("v2/user/")
    fun getUser(@Header("Authorization") token:String
    ):Call<UserData>
}