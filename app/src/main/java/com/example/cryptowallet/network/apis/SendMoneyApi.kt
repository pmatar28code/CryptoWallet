package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.network.classesapi.SendMoney
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface SendMoneyApi {
    @Headers("Accept: application/json")
    @POST("v2/accounts/ef804b1f-c74d-5679-bda7-9b2a25863f22/transactions")
    @FormUrlEncoded
    fun sendMoney(
        @Field("type")type:String,//send
        @Field("to")to:String,//address to send
        @Field("amount")amount:String,
        @Field("currency")currency:String,//BTC
        @Field("idem")idem:String//create unique code with date/time string
    ): Call<SendMoney.Data>
}