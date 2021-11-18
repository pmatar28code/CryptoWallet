package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.network.apis.LogoutApi
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LogoutNetwork @Inject constructor(
    private val superNetwork: SuperNetwork
) {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val accessTokenInterceptor =
        TokenAuthorizationInterceptor(
            accessTokenProvider
        )
    private val tokenRefreshAuthenticatorCoinBase =
        TokenRefreshAuthenticatorCoinBase(
            accessTokenProvider
        )
    private val client = superNetwork.buildOkHttpClient(
        accessTokenInterceptor,
        tokenRefreshAuthenticatorCoinBase
    )
    private val logoutApi: LogoutApi
        get(){
            return superNetwork.buildRetrofit(client)
                .create(LogoutApi::class.java)
        }

    private class LogoutCallBack(
        private val onSuccess:(Any) -> Unit): Callback<Any> {
        override fun onResponse(call: Call<Any>, response: Response<Any>) {

            onSuccess("logout")
        }
        override fun onFailure(call: Call<Any>, t: Throwable) {
            Log.e("On Failure Logout:","$t")
        }
    }
    fun logout (onSuccess: (Any) -> Unit){
        val token = accessTokenProvider.token()?.access_token ?:""

        logoutApi.logout("Bearer $token").enqueue(LogoutCallBack(onSuccess)) //getUser(token).enqueue(AddressCallBack(onSuccess))
    }
}