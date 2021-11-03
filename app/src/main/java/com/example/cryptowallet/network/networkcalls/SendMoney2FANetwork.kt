package com.example.cryptowallet.network.networkcalls

import android.util.Log
import com.example.cryptowallet.Repository
import com.example.cryptowallet.network.apis.Apis
import com.example.cryptowallet.network.classesapi.SendMoney
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorization2FAInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticator2FACoinbase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SendMoney2FANetwork @Inject constructor(
    private val superNetwork: SuperNetwork
) {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val tokenAuthorization2FAInterceptor =
        TokenAuthorization2FAInterceptor(
            accessTokenProvider
        )
    private val tokenRefreshAuthenticator2FACoinbase =
        TokenRefreshAuthenticator2FACoinbase(
            accessTokenProvider
        )
    private val client = superNetwork.buildOkHttpClient2FA(
        tokenAuthorization2FAInterceptor,
        tokenRefreshAuthenticator2FACoinbase
    )
    private val apis: Apis
        get(){
            return superNetwork.buildRetrofit(client)
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

        apis.sendMoney ("Bearer $token",token2fa,accountId,
            "send",to,amount,currency).enqueue(
            SendMoneyCallBack(onSuccess)
        )
    }
}