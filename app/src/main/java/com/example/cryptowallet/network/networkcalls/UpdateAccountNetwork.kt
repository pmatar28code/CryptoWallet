package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.network.apis.UpdateAccountApi
import com.example.cryptowallet.network.classesapi.UpdateAccount
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST

object UpdateAccountNetwork {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(TokenAuthorizationInterceptor(accessTokenProvider))
        .authenticator(TokenRefreshAuthenticatorCoinBase(accessTokenProvider))
        .build()
    private val updateAccountApi: UpdateAccountApi
        get(){
            return Retrofit.Builder()
                .baseUrl("https://api.coinbase.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(UpdateAccountApi::class.java)
        }
    private class UpdateAccountCallBack(
        private val onSuccess:(UpdateAccount.Data) -> Unit): Callback<UpdateAccount.Data> {
        override fun onResponse(call: Call<UpdateAccount.Data>, response: Response<UpdateAccount.Data>) {
           Log.e("ON RESPONSE UPDATEACCOUNT NETWORK","code: ${response.code()}")
            var updateAccountTest = UpdateAccount.Data(
                createdAt = response.body()?.createdAt,
                balance = response.body()?.balance,
                currency = response.body()?.currency,
                id = response.body()?.id,
                name = response.body()?.name,
                primary = response.body()?.primary,
                resource = response.body()?.resource,
                resourcePath = response.body()?.resourcePath,
                type = response.body()?.type,
                updatedAt = response.body()?.updatedAt
            )
            Log.e("ON Response Update Account:"," ${response.body()?.name}, ${response.body()?.createdAt}")

            onSuccess(updateAccountTest)
        }

        override fun onFailure(call: Call<UpdateAccount.Data>, t: Throwable) {
            Log.e("On Failure Address:","$t")
        }
    }

    fun updateAccount (onSuccess: (UpdateAccount.Data) -> Unit){
        val token = AccessTokenProviderImp().token()?.access_token?:""
        Log.e("UPDATE ACCOUNT REFRESH TOKEN FROM Actual TOKEN:", token)
        /*val updateAccountTest = UpdateAccount.Data(
            balance = UpdateAccount.Data.Balance("8","BTX"),
            createdAt = "",
            currency = "BTC",
            id ="",
            name ="test name 2",
            primary = false,
            resource = "",
            resourcePath = "",
            type = "wallet",
            updatedAt = ""
        )*/
        updateAccountApi.updateAccount (token,"BTC Wallet").enqueue(UpdateAccountCallBack(onSuccess))
    }
}