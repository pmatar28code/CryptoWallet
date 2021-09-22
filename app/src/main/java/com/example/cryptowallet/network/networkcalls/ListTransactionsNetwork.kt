package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.Repository
import com.example.cryptowallet.network.apis.ListTransactionsApi
import com.example.cryptowallet.network.classesapi.ListTransactions
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ListTransactionsNetwork @Inject constructor(
    private val superNetwork: SuperNetwork
    ) {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val tokenAuthorizationInterceptor =
        TokenAuthorizationInterceptor(
            accessTokenProvider
        )
    private val tokenRefreshAuthenticatorCoinBase =
        TokenRefreshAuthenticatorCoinBase(
            accessTokenProvider
        )

    private val client = superNetwork.buildOkHttpClient(
        tokenAuthorizationInterceptor,
        tokenRefreshAuthenticatorCoinBase
    )
    private val listTransactionsApi: ListTransactionsApi
        get() {
            return superNetwork.buildRetrofit(client)
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