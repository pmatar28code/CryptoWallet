package com.example.cryptowallet.network.apis

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CryptoIconApi {
    @GET("icon/{currency}}/128x128")
    fun getIcons(
        @Path("currency") currency:String
    ):Call<String>
}