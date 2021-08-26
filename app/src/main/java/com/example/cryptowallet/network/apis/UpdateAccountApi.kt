package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.UpdateAccount
import retrofit2.Call
import retrofit2.http.*

var id ="ef804b1f-c74d-5679-bda7-9b2a25863f22"
interface UpdateAccountApi {
    @Headers("Accept: application/json")
    @PUT("v2/accounts/ef804b1f-c74d-5679-bda7-9b2a25863f22")
    @FormUrlEncoded
    fun updateAccount(
        @Header("Authorization") token:String,
        @Field("name") name:String
    ): Call<UpdateAccount.Data>
}