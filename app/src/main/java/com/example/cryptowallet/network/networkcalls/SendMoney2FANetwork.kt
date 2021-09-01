package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.Repository
import com.example.cryptowallet.network.apis.SendMoney2FAAPI
import com.example.cryptowallet.network.classesapi.SendMoney
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorization2FAInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticator2FACoinbase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SendMoney2FANetwork {
    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val accessTokenProvider = AccessTokenProviderImp()
    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(TokenAuthorization2FAInterceptor(accessTokenProvider))
        .addInterceptor(logger)
        .authenticator(TokenRefreshAuthenticator2FACoinbase(accessTokenProvider))
        .build()
    private val sendMoney2FAAPI: SendMoney2FAAPI
        get(){
            return Retrofit.Builder()
                .baseUrl("https://api.coinbase.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(SendMoney2FAAPI::class.java)
        }
    private class SendMoneyCallBack(
        private val onSuccess:(SendMoney.Data) -> Unit): Callback<SendMoney.Data> {
        override fun onResponse(call: Call<SendMoney.Data>, response: Response<SendMoney.Data>) {
            val sendMoneyData = SendMoney.Data(
                amount = response.body()?.amount,
                createdAt = response.body()?.createdAt,
                description = response.body()?.description,
                details = response.body()?.details,
                id = response.body()?.id,
                nativeAmount = response.body()?.nativeAmount,
                network = response.body()?.network,
                resource = response.body()?.resource,
                resourcePath = response.body()?.resourcePath,
                status = response.body()?.status,
                to = response.body()?.to,
                type = response.body()?.type,
                updatedAt = response.body()?.updatedAt
            )
            Repository.sendMoneyDataObj = sendMoneyData
            if(response.code() == 400){
                sendMoneyData.id = "0"
                onSuccess(sendMoneyData)
            }else{
                Repository.sendMoneyDataObj = sendMoneyData
                onSuccess(sendMoneyData)
            }
        }
        override fun onFailure(call: Call<SendMoney.Data>, t: Throwable) {
            Log.e("On Failure Send Money 2FA Network:","$t")
        }
    }
    fun sendMoney (onSuccess: (SendMoney.Data) -> Unit){
        val token = AccessTokenProviderImp().token()?.access_token?:""
        val token2fa = Repository.token2fa
        val accountId = Repository.sendMoneyAccountId
        val to = Repository.sendMonetTo
        val currency = Repository.sendMoneyCurrency
        val amount = Repository.sendMoneyAmount

        sendMoney2FAAPI.sendMoney ("Bearer $token",token2fa,accountId,
            "send",to,amount,currency).enqueue(
            SendMoneyCallBack(onSuccess)
        )
    }
}