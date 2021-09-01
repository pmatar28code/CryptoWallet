package com.example.cryptowallet.oauth

import android.util.Log
import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.utilities.EncSharedPreferences
import com.example.cryptowallet.utilities.Utility
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticatorCoinBase(
    private val tokenProvider: AccessTokenProvider
) : Authenticator {
    private val keyStringAccessKey = "Access_key"
    private val utilityApplicationContext = Utility.getInstance()?.applicationContext

    override fun authenticate(route: Route?, response: Response): Request? {
        val token = tokenProvider.token() ?: return null
        synchronized(this) {
            val newToken = tokenProvider.token()
            if (response.request.header("Authorization") != null) {

                if (newToken != token) {
                    return response.request
                        .newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer ${newToken?.access_token}")
                        .build()
                }

                var updatedToken: AccessToken? = null
                tokenProvider.refreshToken {
                    if(it){
                        val stringUpdatedToken = utilityApplicationContext?.let { it1 ->
                            EncSharedPreferences.getValueString(keyStringAccessKey,
                                it1
                            )
                        }
                            ?:""
                        updatedToken = EncSharedPreferences.convertJsonStringToTestClass(stringUpdatedToken)
                        Log.e("NEW UPDATED TOKEN ON AUTHENTICATOR COINBASE","$updatedToken")
                    }else{
                        Log.e("RECEIVED FALSE FROM REFRESH ON AUTHENTICATOR COINBASE","FALSE DO NOTHING")
                    }
                }
                return response.request
                    .newBuilder()
                    .header("Authorization", "Bearer ${updatedToken?.access_token}")
                    .build()
            }
        }
        return null
    }
}