package com.example.cryptowallet.network.networkcalls

import com.example.cryptowallet.network.apis.Apis
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class SuperNetwork @Inject constructor() {
    private var client : OkHttpClient ?= null
    private var client2Fa : OkHttpClient ?= null
    private var retrofit:Apis ?= null

    fun buildOkHttpClient(
        interceptor: Interceptor,
        authenticator: Authenticator
    ): OkHttpClient{
        if(client == null) {
            client = OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .authenticator(authenticator)
                .build()
        }
        return client as OkHttpClient
    }

    fun buildOkHttpClient2FA(
        interceptor2FA: Interceptor,
        authenticator2FA: Authenticator
    ): OkHttpClient{
        if(client2Fa == null) {
            client2Fa = OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor2FA)
                .authenticator(authenticator2FA)
                .build()
        }
        return client2Fa as OkHttpClient
    }

    fun buildSimpleOkHttpClient():OkHttpClient{
        return OkHttpClient()
    }

    fun buildRetrofit(client:OkHttpClient):Apis{
        if(retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.coinbase.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(Apis::class.java)
        }
        return retrofit!!
    }
}