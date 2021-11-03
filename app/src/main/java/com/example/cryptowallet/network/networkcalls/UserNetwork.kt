package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.network.apis.Apis
import com.example.cryptowallet.network.classesapi.UserData
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UserNetwork @Inject constructor(
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
    private val apis: Apis
        get(){
            return superNetwork.buildRetrofit(client)
        }

    private class UserCallBack(
        private val onSuccess:(UserData.Data) -> Unit): Callback<UserData> {
        override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
            val newClient = UserData.Data(
                name = response.body()?.data?.name ?: "",
                avatarUrl = response.body()?.data?.avatarUrl ?: "",
                id = response.body()?.data?.id ?: "",
                profileBio = response.body()?.data?.profileBio ?: "",
                profileLocation = response.body()?.data?.profileLocation ?: "",
                profileUrl = response.body()?.data?.profileUrl ?: "",
                resource = response.body()?.data?.resource ?: "",
                resourcePath = response.body()?.data?.resourcePath ?: "",
                username = response.body()?.data?.username ?: ""
            )
            onSuccess(newClient)
        }
        override fun onFailure(call: Call<UserData>, t: Throwable) {
            Log.e("On Failure Address:","$t")
        }
    }
    fun getUser (onSuccess: (UserData.Data) -> Unit){
        val token = accessTokenProvider.token()?.access_token ?:""

        apis.getUser("Bearer $token").enqueue(UserCallBack(onSuccess)) //getUser(token).enqueue(AddressCallBack(onSuccess))
    }
}