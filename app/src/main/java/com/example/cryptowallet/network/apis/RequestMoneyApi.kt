package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.RequestMoney
import com.example.cryptowallet.network.classesapi.RequestMoneyApiBody
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
import kotlin.reflect.jvm.internal.impl.load.java.Constant

/*
curl https://api.coinbase.com/v2/accounts/2bbf394c-193b-5b2a-9155-3b4732659ede/transactions /
  -X POST \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer abd90df5f27a7b170cd775abf89d632b350b7c1c9d53e08b340cd9832ce52c2c' \
  -d '{
    "type": "request",
    "to": "email@example.com",
    "amount": "1",
    "currency": "BTC"
  }'
*/
/*
interface RequestMoneyApi {
    @Headers("Accept: application/json")
    @POST("v2/accounts/ef804b1f-c74d-5679-bda7-9b2a25863f22/transactions")
    @FormUrlEncoded
    fun requestMoney(
        @Header("Authorization") token:String,
        @Field("type") type:String,
        @Field("to") to:String,
        @Field("amount") amount:String,
        @Field("currency") currency:String,
        @Field("description") description:String
    ):Call<RequestMoney.Data>
}
 */
interface RequestMoneyApi {
    @Headers("Accept: application/vnd.api+json")
    @POST("v2/accounts/ef804b1f-c74d-5679-bda7-9b2a25863f22/transactions")
    fun requestMoney(
        @Body body:RequestMoneyApiBody
    ):Call<RequestMoney.Data>
}