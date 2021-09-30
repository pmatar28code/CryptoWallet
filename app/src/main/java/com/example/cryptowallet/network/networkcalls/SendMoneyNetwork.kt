package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.Repository
import com.example.cryptowallet.network.apis.SendMoneyApi
import com.example.cryptowallet.network.classesapi.SendMoney
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SendMoneyNetwork @Inject constructor(
    private val superNetwork: SuperNetwork
){
    private val accessTokenProvider = AccessTokenProviderImp()
    private val tokenAuthorizationInterceptor =
        TokenAuthorizationInterceptor(
            accessTokenProvider
        )
    private val tokenRefreshAuthenticatorCoinBase =
        TokenRefreshAuthenticatorCoinBase(
            accessTokenProvider
        )
    private val client = superNetwork.buildOkHttpClient(
        tokenAuthorizationInterceptor,
        tokenRefreshAuthenticatorCoinBase
    )
    private val sendMoneyApi: SendMoneyApi
        get(){
            return superNetwork.buildRetrofit(client)
                .create(SendMoneyApi::class.java)
        }
    private class SendMoneyCallBack(
        private val onSuccess:(SendMoney.Data) -> Unit): Callback<SendMoney.Data> {
        override fun onResponse(call: Call<SendMoney.Data>, response: Response<SendMoney.Data>) {
            val sendMoneyData = SendMoney.Data(
                amount = response.body()?.amount,
                createdAt = response.body()?.createdAt,
                description = response.body()?.description,
                details = response.body()?.details,
                id=response.body()?.id,
                nativeAmount= response.body()?.nativeAmount,
                network = response.body()?.network,
                resource = response.body()?.resource,
                resourcePath = response.body()?.resourcePath,
                status = response.body()?.status,
                to= response.body()?.to,
                type=response.body()?.type,
                updatedAt = response.body()?.updatedAt
            )

            if(response.code() == 402){
                Repository.repoSendMoneyResponseCode = response.code()
                onSuccess(sendMoneyData)
            }else{
                Repository.repoSendMoneyResponseCode = 0
                onSuccess(sendMoneyData)
            }
        }

        override fun onFailure(call: Call<SendMoney.Data>, t: Throwable) {
            Log.e("On Failure Send Money Network:","$t")
        }
    }
    fun sendMoney (onSuccess: (SendMoney.Data) -> Unit){
        val token = AccessTokenProviderImp().token()?.access_token?:""
        val accountId = Repository.sendMoneyAccountId
        val to = Repository.sendMonetTo
        val currency = Repository.sendMoneyCurrency
        val amount = Repository.sendMoneyAmount

        sendMoneyApi.sendMoney (token,accountId,"send",to,amount,currency).enqueue(
            SendMoneyCallBack(onSuccess)
        )
    }
}