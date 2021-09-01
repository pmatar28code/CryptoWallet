package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.SendMoney
import retrofit2.Call
import retrofit2.http.*

interface SendMoneyApi {
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
}