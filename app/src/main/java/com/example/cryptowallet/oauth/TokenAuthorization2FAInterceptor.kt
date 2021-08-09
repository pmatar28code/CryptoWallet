package com.example.cryptowallet.oauth

import android.util.Log
import com.example.cryptowallet.Repository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TokenAuthorization2FAInterceptor(
    private val authorizationRepository: AccessTokenProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().signedRequest()
        return chain.proceed(newRequest)
    }
    private fun Request.signedRequest(): Request {
        val accessToken = authorizationRepository.token()?.access_token?:""
        Log.e("ACCESS TOKEN INTERCEPTOR", accessToken)
        return newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .header("CB-2FA-TOKEN", Repository.token2fa)
            .build()
    }
}