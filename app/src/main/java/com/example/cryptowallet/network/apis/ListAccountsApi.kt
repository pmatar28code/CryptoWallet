package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.ListAccounts
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ListAccountsApi {
    @GET("v2/accounts?&limit=100")
    fun getAccounts(@Header("Authorization") token:String
    ):Call<ListAccounts>
}