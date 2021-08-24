package com.example.cryptowallet.network.apis

import androidx.room.Update
import com.example.cryptowallet.network.classesapi.UpdateAccount
import retrofit2.Call
import retrofit2.http.*


//curl https://api.coinbase.com/v2/accounts/82de7fcd-db72-5085-8ceb-bee19303080b \
//-X PUT
//-H 'Content-Type: application/json'
//-H 'Authorization: Bearer abd90df5f27a7b170cd775abf89d632b350b7c1c9d53e08b340cd9832ce52c2c'
//-d '{"name": "New account name"}'
var id ="ef804b1f-c74d-5679-bda7-9b2a25863f22"
interface UpdateAccountApi {
    @Headers("Accept: application/json")
    @PUT("v2/accounts/ef804b1f-c74d-5679-bda7-9b2a25863f22")
    @FormUrlEncoded
    fun updateAccount(
        @Header("Authorization") token:String,
        //@Path("name") name:String,
        @Field("name") name:String
        //@Body post: UpdateAccount.Data
    ): Call<UpdateAccount.Data>
}