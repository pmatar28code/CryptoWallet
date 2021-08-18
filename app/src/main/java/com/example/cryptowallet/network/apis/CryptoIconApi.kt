package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.CryptoIcons
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/*
interface CryptoIconApi {
    //https://api.coinstats.app/
    @GET("public/v1/coins?skip=0&limit=150")
    fun getIcons():Call<CryptoIcons>
}
 */

interface CryptoIconApi {
    //https://api.coinicons.net/
    @GET("icon/{currency}}/128x128")
    fun getIcons(
        @Path("currency") currency:String
    ):Call<String>
}