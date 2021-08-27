package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.Repository
import com.example.cryptowallet.network.apis.AddressApi
import com.example.cryptowallet.network.classesapi.NAddress
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AddressNetwork {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(TokenAuthorizationInterceptor(accessTokenProvider))
        .authenticator(TokenRefreshAuthenticatorCoinBase(accessTokenProvider))
        .build()
    private val addressApi: AddressApi
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.coinbase.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(AddressApi::class.java)
        }

    private class AddressCallBack(
        private val onSuccess: (NAddress.Data) -> Unit
    ) : Callback<NAddress> {
        override fun onResponse(call: Call<NAddress>, response: Response<NAddress>) {
            val newNAddress = NAddress.Data(
                address = response.body()?.data?.address,
                createdAt = response.body()?.data?.createdAt,
                id = response.body()?.data?.id,
                name = response.body()?.data?.name,
                network = response.body()?.data?.network,
                resource = response.body()?.data?.resource,
                resourcePath = response.body()?.data?.resourcePath,
                updatedAt = response.body()?.data?.updatedAt
            )
            onSuccess(newNAddress)
        }

        override fun onFailure(call: Call<NAddress>, t: Throwable) {
            Log.e("On Failure Address:", "$t")
        }
    }

    fun getAddresses(onSuccess: (NAddress.Data) -> Unit) {
        val token = AccessTokenProviderImp().token()?.access_token ?: ""
        val accountId = Repository.accountId
        addressApi.getAddress("Bearer $token", accountId).enqueue(AddressCallBack(onSuccess))
    }
}

