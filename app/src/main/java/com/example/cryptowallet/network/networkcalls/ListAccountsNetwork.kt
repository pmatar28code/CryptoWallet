package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.network.apis.ListAccountsApi
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ListAccountsNetwork @Inject constructor(
    private val superNetwork: SuperNetwork
    ){
    private val accessTokenProvider = AccessTokenProviderImp()
    private val tokenAuthorizationInterceptor =
        TokenAuthorizationInterceptor(
            accessTokenProvider
        )
    private val tokenRefreshAuthenticatorCoinBase =
        TokenRefreshAuthenticatorCoinBase(
        accessTokenProvider)

    private val client = superNetwork.buildOkHttpClient(
        tokenAuthorizationInterceptor,
        tokenRefreshAuthenticatorCoinBase
    )
    private val listAccountsApi: ListAccountsApi
        get() {
            return superNetwork.buildRetrofit(client)
                .create(ListAccountsApi::class.java)
        }

    private class AccountsCallBack(
        private val onSuccess: (List<ListAccounts.Data>) -> Unit
    ) : Callback<ListAccounts> {
        override fun onResponse(call: Call<ListAccounts>, response: Response<ListAccounts>) {
            val listOfAccounts = mutableListOf<ListAccounts.Data>()
            for(item in response.body()?.data!!){
                listOfAccounts.add(item!!)
            }
            onSuccess(listOfAccounts.toList())
        }
        override fun onFailure(call: Call<ListAccounts>, t: Throwable) {
            Log.e("On Failure LIST ACCOUNTS NETWORK:", "This is T : $t")
        }
    }

    fun getAccounts(onSuccess: (List<ListAccounts.Data>) -> Unit) {
        val token = AccessTokenProviderImp().token()?.access_token ?: ""
        listAccountsApi.getAccounts("Bearer $token").enqueue(AccountsCallBack(onSuccess))
    }
}