package com.example.cryptowallet.network.networkcalls

import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class SuperNetwork @Inject constructor() {
    private var client : OkHttpClient ?= null
    private var retrofit:Retrofit ?= null

    fun buildOkHttpClient(
        interceptor: Interceptor,
        authenticator: Authenticator
    ): OkHttpClient{
       client = OkHttpClient.Builder()
            .addNetworkInterceptor(interceptor)
            .authenticator(authenticator)
            .build()
        return client as OkHttpClient
    }

    fun buildSimpleOkHttpClient():OkHttpClient{
        return OkHttpClient()
    }

    fun buildRetrofit(client:OkHttpClient):Retrofit{
        retrofit = Retrofit.Builder()
            .baseUrl("https://api.coinbase.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit as Retrofit
    }
}