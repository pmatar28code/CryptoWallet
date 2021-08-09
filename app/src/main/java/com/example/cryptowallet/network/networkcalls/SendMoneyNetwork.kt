package com.example.cryptowallet.network.networkcalls

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cryptowallet.network.apis.SendMoneyApi
import com.example.cryptowallet.network.classesapi.SendMoney
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object SendMoneyNetwork {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(TokenAuthorizationInterceptor(accessTokenProvider))
        .authenticator(TokenRefreshAuthenticatorCoinBase(accessTokenProvider))
        .build()
    private val sendMoneyApi: SendMoneyApi
        get(){
            return Retrofit.Builder()
                .baseUrl("https://api.coinbase.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(SendMoneyApi::class.java)
        }
    private class SendMoneyCallBack(
        private val onSuccess:(SendMoney.Data) -> Unit): Callback<SendMoney.Data> {
        override fun onResponse(call: Call<SendMoney.Data>, response: Response<SendMoney.Data>) {
            Log.e("ON Response Send Money Network:","${response.body()?.details} status: ${response.body()?.status}")

        }

        override fun onFailure(call: Call<SendMoney.Data>, t: Throwable) {
            Log.e("On Failure Send Money Network:","$t")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMoney (onSuccess: (SendMoney.Data) -> Unit){
        //val refreshToken = AccessTokenProviderImp().token()?.refresh_token?:""
        var idem = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
        //Log.e("REFRESH NETWORK REFRESH TOKEN FROM Actual TOKEN:", refreshToken)
        sendMoneyApi.sendMoney ("send","37fh2yPJxcEveHYk6gaQBjTTi21KuZZsc3","0.1","USD",idem).enqueue(
            SendMoneyCallBack(onSuccess)
        )
    }
}