package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.Repository
import com.example.cryptowallet.network.apis.ListTransactionsApi
import com.example.cryptowallet.network.classesapi.ListTransactions
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ListTransactionsNetwork {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .addNetworkInterceptor(TokenAuthorizationInterceptor(accessTokenProvider))
        .authenticator(TokenRefreshAuthenticatorCoinBase(accessTokenProvider))
        .build()
    private val listTransactionsApi: ListTransactionsApi
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.coinbase.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ListTransactionsApi::class.java)
        }

    private class TransactionsCallBack(
        private val onSuccess: (List<ListTransactions.Data>) -> Unit
    ) : Callback<ListTransactions> {
        override fun onResponse(call: Call<ListTransactions>, response: Response<ListTransactions>) {
            var listOfTransactions = mutableListOf<ListTransactions.Data>()
            if(response.body()?.data != null) {
                for (item in response.body()?.data!!) {
                    listOfTransactions.add(item!!)
                }
                onSuccess(listOfTransactions.toList())
            }else{
                listOfTransactions = emptyList<ListTransactions.Data>().toMutableList()
                onSuccess(listOfTransactions)
            }
        }
        override fun onFailure(call: Call<ListTransactions>, t: Throwable) {
            Log.e("On Failure LIST Transactions NETWork:", "This is T : $t")
        }
    }

    fun getTransactions(onSuccess: (List<ListTransactions.Data>) -> Unit) {
        val token = AccessTokenProviderImp().token()?.access_token ?: ""
        val accountId = Repository.setTransactionIdForSpecificNetworkRequest
        listTransactionsApi.getTransactions("Bearer $token",accountId).enqueue(TransactionsCallBack(onSuccess))
    }
}