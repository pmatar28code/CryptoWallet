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

    private class ShowAddressesCallBack(
        private val onSuccess: (List<ShowAddresses.Data>) -> Unit
    ) : Callback<ShowAddresses> {
        override fun onResponse(call: Call<ShowAddresses>, response: Response<ShowAddresses>) {
            val listOfAddresses = mutableListOf<ShowAddresses.Data>()
                for (address in response.body()?.data!!) {
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
        showAddressesApi.getAddress("Bearer $token").enqueue(ShowAddressesCallBack(onSuccess))
    }
}