package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.SendMoney
import retrofit2.Call
import retrofit2.http.*

interface SendMoney2FAAPI {
   // @Headers("Accept: application/json","Authorization","CB-2FA-TOKEN")
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
       //@Field("two_factor_token")toFactorToken:String,
       //@Field("idem")idem:String//create unique code with date/time string
    ): Call<SendMoney.Data>
}