package com.example.cryptowallet.network.apis

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/*
curl https://api.coinbase.com/v2/accounts/2bbf394c-193b-5b2a-9155-3b4732659ede/transactions/2e9f48cd-0b05-5f7c-9056-17a8acb408ad/complete /
  -X POST \
  -H 'Authorization: Bearer abd90df5f27a7b170cd775abf89d632b350b7c1c9d53e08b340cd9832ce52c2c'
*/
interface CompleteRequestMoneyApi {
    @POST("v2/accounts/ef804b1f-c74d-5679-bda7-9b2a25863f22/transactions/{requestId}}/complete")
    fun completeRequestMoney(
        @Header("Authorization") token:String,
        @Path("requestId") requestId:String,
        ):Call<Void>
}