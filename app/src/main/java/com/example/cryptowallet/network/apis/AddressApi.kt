package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.NAddress
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AddressApi {
    companion object{
        const val postValue = "v2/accounts/{id}/addresses"
    }
    @POST(postValue)
    fun getAddress(
        @Header("Authorization") token:String,
        @Path("id")id:String
    ): Call<NAddress>
}
