package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.AccessToken
import retrofit2.Call
import retrofit2.http.*

interface CoinBaseClient {
    @Headers("Accept: application/json")
    @POST("oauth/token")
    @FormUrlEncoded
    fun getToken(
        @Field("grant_type")grant_type:String,
        @Field("code")code:String,
        @Field("client_id")client_id:String,
        @Field("client_secret")client_secret:String,
        @Field("redirect_uri")redirect_uri:String
    ): Call<AccessToken>
}