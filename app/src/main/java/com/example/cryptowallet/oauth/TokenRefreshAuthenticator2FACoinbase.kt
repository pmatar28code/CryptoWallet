package com.example.cryptowallet.oauth

import android.util.Log
import com.example.cryptowallet.Repository
import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.utilities.EncSharedPreferences
import com.example.cryptowallet.utilities.Utility
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator2FACoinbase(
        private val tokenProvider: AccessTokenProvider
    ) : Authenticator {
    private val keyStringAccessKey = "Access_key"
    private val utilityApplicationContext = Utility.getInstance()?.applicationContext

        override fun authenticate(route: Route?, response: Response): Request? {
            // We need to have a token in order to refresh it.
            val token = tokenProvider.token() ?: return null

            synchronized(this) {
                val newToken = tokenProvider.token()

                // Check if the request made was previously made as an authenticated request.
                if (response.request.header("Authorization") != null) {

                    // If the token has changed since the request was made, use the new token.
                    if (newToken != token) {
                        return response.request
                            .newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer ${newToken?.access_token}")
                            .addHeader("CB-2FA-TOKEN", Repository.token2fa)
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
                            Log.e("NEW UPDATED TOKEN ON AUTHENTICATOR CONBASE","$updatedToken")
                        }else{
                            Log.e("RECIEVED FALSE FROM REFRESG ON AUTHENTICATOR COINBASE","FALSE DO NOTHING")
                            //updatedToken = null
                            //Log.e("NEW UPDATED TOKEN ON AUTHENTICATOR CONBASE","$updatedToken")

                        }
                    }
                    // Retry the request with the new token.
                    return response.request
                        .newBuilder()
                        //.removeHeader("Authorization")
                        .header("Authorization", "Bearer ${updatedToken?.access_token}")
                        .header("CB-2FA-TOKEN", Repository.token2fa)
                        .build()
                }
            }
            return null
        }
    }
