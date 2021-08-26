package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.network.apis.RequestMoneyApi
import com.example.cryptowallet.network.classesapi.RequestMoney
import com.example.cryptowallet.network.classesapi.RequestMoneyApiBody
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RequestMoneyNetwork {
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    private val accessTokenProvider = AccessTokenProviderImp()
    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(TokenAuthorizationInterceptor(accessTokenProvider))
        .addInterceptor(logger)
        .authenticator(TokenRefreshAuthenticatorCoinBase(accessTokenProvider))
        .build()
    private val requestMoneyApi: RequestMoneyApi
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.coinbase.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RequestMoneyApi::class.java)
        }
    private class RequestMoneyCallBack(
        private val onSuccess: (RequestMoney.Data) -> Unit
    ) : Callback<RequestMoney.Data> {
        override fun onResponse(call: Call<RequestMoney.Data>, response: Response<RequestMoney.Data>) {
            Log.e("ON Response REQUEST MONEY:", "code: ${response.code()} ${response.body()?.to} , amount ${response.body()?.amount}, successfull? ${response.isSuccessful}")
            val newRequestMoney = RequestMoney.Data(
                type = response.body()?.type,
                amount = response.body()?.amount,
                createdAt = response.body()?.createdAt,
                updatedAt = response.body()?.updatedAt,
                id = response.body()?.id,
                description = response.body()?.description,
                to = response.body()?.to,
                details = response.body()?.details,
                nativeAmount = response.body()?.nativeAmount,
                resourcePath = response.body()?.resourcePath,
                resource = response.body()?.resource,
                status = response.body()?.status

            )
            onSuccess(newRequestMoney)
        }

        override fun onFailure(call: Call<RequestMoney.Data>, t: Throwable) {
            Log.e("On Failure Address:", "$t")
        }
    }
    fun getRequestMoney(onSuccess: (RequestMoney.Data) -> Unit) {
        val type = "request"
        val token = AccessTokenProviderImp().token()?.access_token ?: ""
        Log.e("On Actual REQUEST MONEY NETWORK TOKEN:", token)
        val requestMoney = RequestMoneyApiBody(
            "request",
            "Phil@nevie.com",
            ".0001",
            "BTC",
            "this is a test for requesting money =)"
        )
        val test = JSONObject()
        test.put("type","request")
        test.put("to","phil@nevie.com")
        test.put("amount",".0001")
        test.put("currency","BTC")
        test.put("description","request test")
        requestMoneyApi.requestMoney(requestMoney).enqueue(RequestMoneyCallBack(onSuccess))
    }
}