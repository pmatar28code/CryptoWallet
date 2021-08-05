package com.example.cryptowallet.network.apis

import com.example.cryptowallet.network.classesapi.NAddress
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface AddressApi {
    companion object{
        const val postValue = "v2/accounts/0b125b64-619e-5cf8-a719-963534b52bca/addresses"
    }
    @POST(postValue)
    fun getAddress(
        @Header("Authorization") token:String): Call<NAddress>
}
