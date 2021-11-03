package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.Repository
import com.example.cryptowallet.network.apis.Apis
import com.example.cryptowallet.network.classesapi.NAddress
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AddressNetwork @Inject constructor(
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
    var client = superNetwork.buildOkHttpClient(
        tokenAuthorizationInterceptor,
        tokenRefreshAuthenticatorCoinBase
        )
    private val apis: Apis
        get() {
            return superNetwork.buildRetrofit(client)
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
        apis.getAddress("Bearer $token", accountId).enqueue(AddressCallBack(onSuccess))
    }
}

