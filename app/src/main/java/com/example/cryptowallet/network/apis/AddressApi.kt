package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.NAddress
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface AddressApi {
    companion object{
        const val postValue = "v2/accounts/ef804b1f-c74d-5679-bda7-9b2a25863f22/addresses"
    }
    @POST(postValue)
    fun getAddress(
        @Header("Authorization") token:String): Call<NAddress>
}
