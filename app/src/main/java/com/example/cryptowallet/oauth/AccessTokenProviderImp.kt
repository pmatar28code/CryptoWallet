package com.example.cryptowallet.oauth

import android.util.Log
import com.example.cryptowallet.MainActivity
import com.example.cryptowallet.Repository
import com.example.cryptowallet.network.apis.RefreshTokenApi
import com.example.cryptowallet.network.classesapi.AccessToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class AccessTokenProviderImp : AccessTokenProvider {
    var token: AccessToken?=null
    var newAccessToken: AccessToken?=null

    override fun token(): AccessToken? {
        token = Repository.tempAccessToken
        Log.e("RETURNED TOKEN FUN TOKEN IMP:","$token")
        return token
    }

    override fun refreshToken(refreshCallback: (Boolean) -> Unit) {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://api.coinbase.com/")
            .addConverterFactory(MoshiConverterFactory.create())
        val retrofit = retrofitBuilder.build()
        val refreshClient = retrofit.create(RefreshTokenApi::class.java)
        val refreshTokenCall = AccessTokenProviderImp().token()?.refresh_token?.let {
            refreshClient.refreshToken(
                "refresh_token",
                MainActivity.MY_CLIENT_ID,
                MainActivity.CLIENT_SECRET,
                it
            )
        }
        refreshTokenCall?.enqueue(object: Callback<AccessToken>{
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                Log.e("GOOD RESPONSE IMP:", "TOKEN: ${response.body()?.access_token}")
                newAccessToken = AccessToken(
                    access_token = response.body()?.access_token ?: "",
                    expires_in = response.body()?.expires_in ?: 0,
                    refresh_token = response.body()?.refresh_token ?: "",
                    scope = response.body()?.scope ?: "",
                    token_type = response.body()?.token_type ?: ""
                )
                if (newAccessToken!!.access_token != "" && newAccessToken != null){

                    Repository.tempAccessToken = newAccessToken

                    Log.e("NEW ACCESS TOKen ADDED TO DATABASE FROM IMP", "$newAccessToken")
                    refreshCallback(true)
                }else{
                    refreshCallback(false)
                }
            }
            override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                Log.e("ON FAILURE ReFReSH IMP:","$t")
                refreshCallback(false)
            }
        })
    }
}