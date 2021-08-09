package com.example.cryptowallet.network.networkcalls

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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
            Log.e("ON Response Send Money 2FA Network:","${response.body()?.details} status: ${response.body()?.status}")

        }

        override fun onFailure(call: Call<SendMoney.Data>, t: Throwable) {
            Log.e("On Failure Send Money 2FA Network:","$t")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMoney (onSuccess: (SendMoney.Data) -> Unit){
        val token = AccessTokenProviderImp().token()?.access_token?:""
        var idem = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
        var token2fa = Repository.token2fa
        Log.e("2fa NETWORK  TOKEN sms :", "$token2fa")
        sendMoney2FAAPI.sendMoney ("Bearer $token",Repository.token2fa,"send","3NJJgGJXwjiRNhikFwoCeLpRC2oB6NJ4Wb","0.12","USD").enqueue(
            SendMoneyCallBack(onSuccess)
        )
    }
}