package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.MainActivity
import com.example.cryptowallet.fragments.AuthorizationFragment
import com.example.cryptowallet.network.apis.Apis
import com.example.cryptowallet.network.classesapi.AccessToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GetTokenNetwork @Inject constructor(
   private val superNetwork: SuperNetwork
) {
    private val client = superNetwork.buildSimpleOkHttpClient()
    private val apis: Apis
    get() {
        return superNetwork.buildRetrofit(client)
    }

    private class TokenCallBack(
        private val onSuccess: (AccessToken) -> Unit
    ):Callback<AccessToken>{
        override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
            val accessToken = AccessToken(
                access_token = response.body()?.access_token ?: "",
                token_type = response.body()?.token_type ?: "",
                expires_in = response.body()?.expires_in ?: 0,
                refresh_token = response.body()?.refresh_token ?: "",
                scope = response.body()?.scope ?: ""
            )
            onSuccess(accessToken)
        }

        override fun onFailure(call: Call<AccessToken>, t: Throwable) {
            Log.e("GET TOKEN NETWORK","Did not get token $t")
        }
    }

    fun getFirstToken(onSuccess: (AccessToken) -> Unit){
        val grantType = "authorization_code"
        val code = AuthorizationFragment.code
        val clientId = MainActivity.MY_CLIENT_ID
        val clientSecret =  MainActivity.CLIENT_SECRET
        val redirectUri = MainActivity.MY_REDIRECT_URI

        apis.getToken(
            grantType,
            code,
            clientId,
            clientSecret,
            redirectUri
        ).enqueue(TokenCallBack(onSuccess))
    }
}