package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.SendMoney
import retrofit2.Call
import retrofit2.http.*

interface SendMoneyApi {
    @Headers("Accept: application/json")
    @POST("v2/accounts/ef804b1f-c74d-5679-bda7-9b2a25863f22/transactions")
    @FormUrlEncoded
    fun sendMoney(
        @Header("Authorization")token:String,
        @Field("type")type:String,//send
        @Field("to")to:String,//address to send
        @Field("amount")amount:String,
        @Field("currency")currency:String,//BTC
        //@Field("idem")idem:String//create unique code with date/time string
    ): Call<SendMoney.Data>
}