package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.network.apis.ShowAddressesApi
import com.example.cryptowallet.network.classesapi.ShowAddresses
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ShowAddressesNetwork {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(TokenAuthorizationInterceptor(accessTokenProvider))
        .authenticator(TokenRefreshAuthenticatorCoinBase(accessTokenProvider))
        .build()
    private val showAddressesApi: ShowAddressesApi
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.coinbase.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ShowAddressesApi::class.java)
        }

    private class AddressCallBack(
        private val onSuccess: (List<ShowAddresses.Data>) -> Unit
    ) : Callback<ShowAddresses> {
        override fun onResponse(call: Call<ShowAddresses>, response: Response<ShowAddresses>) {
            //Log.e("ON Response Address:", " ${response.body()?.address}")
            /*
            val addresses = ShowAddresses.Data(
                address = response.body()?.address ?: "",
                createdAt = response.body()?.createdAt ?: "",
                id = response.body()?.id ?: "",
                name = response.body()?.name ?: "",
                network = response.body()?.network ?: "",
                resource = response.body()?.resource ?: "",
                resourcePath = response.body()?.resourcePath ?: "",
                updatedAt = response.body()?.updatedAt ?: "",
            )*/
            //Log.e(
                //"RESPONDED WITH:",
                //"Address: ${addresses.address},${addresses.name} ${response.isSuccessful}"
            //)
            var listOfAddresses = mutableListOf<ShowAddresses.Data>()
            for(address in response.body()?.data!!){
                if (address != null) {
                    listOfAddresses.add(address)
                }
            }
            onSuccess(listOfAddresses)
        }

        override fun onFailure(call: Call<ShowAddresses>, t: Throwable) {
            Log.e("On Failure Address:", "$t")
        }
    }

    fun getAddresses(onSuccess: (List<ShowAddresses.Data>) -> Unit) {
        val token = AccessTokenProviderImp().token()?.access_token ?: ""
        Log.e("On Actual ADDRESS NETWORK TOKEN:", token)
        showAddressesApi.getAddress("Bearer $token").enqueue(AddressCallBack(onSuccess))
    }
}