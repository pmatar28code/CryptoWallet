package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.network.apis.RefreshTokenApi
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RefreshNetwork {
    const val MY_CLIENT_ID = "2bc5e4b5b4446ad9b730b8561f7aeff463e648cd9146ffb6b5dd885164c300f1"
    const val CLIENT_SECRET = "411f668a4b106cde166902e8b73c02d7938a501b6e90f6b8ddd2809c4250cba7"
    private val accessTokenProvider = AccessTokenProviderImp()
    private val client = OkHttpClient()
    private val refreshTokenApi: RefreshTokenApi
    get(){
        return Retrofit.Builder()
            .baseUrl("https://api.coinbase.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(RefreshTokenApi::class.java)
    }
    private class RefreshCallBack(
        private val onSuccess:(AccessToken) -> Unit): Callback<AccessToken> {
        override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
            val newRefreshedToken = response.body()?.access_token?.let {
                response.body()?.expires_in?.let { it1 ->
                    response.body()?.refresh_token?.let { it2 ->
                        response.body()?.scope?.let { it3 ->
                            response.body()?.token_type?.let { it4 ->
                                AccessToken(
                                    access_token = it,
                                    expires_in = it1,
                                    refresh_token = it2,
                                    scope = it3,
                                    token_type = it4
                                )
                            }
                        }
                    }
                }
            }
            if (newRefreshedToken != null) {
                onSuccess(newRefreshedToken)
            }
        }

        override fun onFailure(call: Call<AccessToken>, t: Throwable) {
            Log.e("On Failure Address:","$t")
        }
    }

    fun refreshToken (onSuccess: (AccessToken) -> Unit){
        val refreshToken = AccessTokenProviderImp().token()?.refresh_token?:""
        refreshTokenApi.refreshToken("refresh_token", MY_CLIENT_ID, CLIENT_SECRET,refreshToken).enqueue(
            RefreshCallBack(onSuccess)
        )
    }
}