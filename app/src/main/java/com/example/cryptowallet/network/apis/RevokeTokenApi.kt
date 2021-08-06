package com.example.cryptowallet.network.apis

import retrofit2.Call
import retrofit2.http.*

interface RevokeTokenApi {
    @POST("oauth/revoke/")
    @FormUrlEncoded
    fun revokeToken(
        @Field("token") tokenString:String,
        @Field("Authorization") token:String
    ): Call<Void>
}