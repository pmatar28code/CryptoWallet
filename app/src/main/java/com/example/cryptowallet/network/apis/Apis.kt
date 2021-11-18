package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.*
import retrofit2.Call
import retrofit2.http.*

interface Apis {
    companion object{
        const val postValue = "v2/accounts/{id}/addresses"
    }
    @POST(postValue)
    fun getAddress(
        @Header("Authorization") token:String,
        @Path("id")id:String
    ): Call<NAddress>

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

    @GET("v2/user/")
    fun getUser(@Header("Authorization") token:String
    ):Call<UserData>

    @GET("v2/accounts?&limit=150")
    fun getAccounts(@Header("Authorization") token:String
    ):Call<ListAccounts>

    @GET("v2/accounts/{account_id}/transactions?&limit=199")
    fun getTransactions(
        @Header("Authorization") token:String,
        @Path("account_id") account_id:String
    ): Call<ListTransactions>

    @Headers("Accept: application/json")
    @POST("oauth/token")
    @FormUrlEncoded
    fun refreshToken(
        @Field("grant_type") refresh_token:String,
        @Field("client_id") YOUR_CLIENT_ID:String,
        @Field("client_secret")YOUR_CLIENT_SECRET:String,
        @Field("refresh_token")REFRESH_TOKEN:String
    ): Call<AccessToken>

    @POST("v2/accounts/{id}/transactions")
    @FormUrlEncoded
    fun sendMoney(
        @Header("Authorization")token:String,
        @Header("CB-2FA-TOKEN")token2fa:String,
        @Path("id")id:String,
        @Field("type")type:String,//send
        @Field("to")to:String,//address to send
        @Field("amount")amount:String,
        @Field("currency")currency:String,//BTC
    ): Call<SendMoney.Data>

    @Headers("Accept: application/json")
    @POST("v2/accounts/{id}/transactions")
    @FormUrlEncoded
    fun sendMoney(
        @Header("Authorization")token:String,
        @Path("id") id:String,
        @Field("type")type:String,//send
        @Field("to")to:String,//address to send
        @Field("amount")amount:String,
        @Field("currency")currency:String,//BTC
    ): Call<SendMoney.Data>

    @Headers("Accept: application/json")
    @GET("oauth/revoke")
    fun logout(@Header("Authorization") token:String
    ):Call<Any>

}