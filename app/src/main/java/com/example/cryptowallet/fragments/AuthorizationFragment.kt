package com.example.cryptowallet.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.cryptowallet.MainActivity
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentAuthorizationBinding
import com.example.cryptowallet.network.apis.CoinBaseClient
import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.network.networkcalls.UserNetwork
import com.example.cryptowallet.utilities.EncSharedPreferences
import com.example.cryptowallet.utilities.Utility
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthorizationFragment: Fragment(R.layout.fragment_authorization) {
    companion object {
        private const val MY_CLIENT_ID = "c77416def5b58698219596f44ecf6236658c426805a522d517f45867b0348188"
        const val urlString = "https://www.coinbase.com/oauth/authorize?client_id=$MY_CLIENT_ID&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&account=all&scope=wallet:accounts:read wallet:addresses:create wallet:addresses:read wallet:accounts:update wallet:accounts:create wallet:transactions:send wallet:transactions:request wallet:transactions:read&meta[send_limit_amount]=1&meta[send_limit_currency]=USD&meta[send_limit_period]=day"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAuthorizationBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        val webView: WebView = binding.webViewAuthorization
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                var code = request.url.toString()
                if (code.contains("?") && code.length == 106) {
                    code = code.removeRange(0, 41)
                    code = code.dropLast(1)
                    Utility.getInstance()?.applicationContext?.let {
                        EncSharedPreferences.saveToEncryptedSharedPrefsString(
                            "Auth_code", code,
                            it
                        )
                    }
                    getTokenNetworkRequest(code)
                    getUserAndListAccountsFromNetwork()
                    return true
                }
                return false
            }
        }
        webView.loadUrl(urlString)
    }

    private fun getTokenNetworkRequest(code: String) {
        val logger = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://api.coinbase.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
        val retrofit = retrofitBuilder.build()
        val coinBaseClient = retrofit.create(CoinBaseClient::class.java)
        val accessTokenCall = coinBaseClient.getToken(
            "authorization_code",
            code,
            MainActivity.MY_CLIENT_ID,
            MainActivity.CLIENT_SECRET,
            MainActivity.MY_REDIRECT_URI
        )
        accessTokenCall?.enqueue(object : Callback<AccessToken> {
            override fun onResponse(
                call: Call<AccessToken>,
                response: Response<AccessToken>
            ){
                val accessToken = AccessToken(
                    access_token = response.body()?.access_token ?: "",
                    token_type = response.body()?.token_type ?: "",
                    expires_in = response.body()?.expires_in ?: 0,
                    refresh_token = response.body()?.refresh_token ?: "",
                    scope = response.body()?.scope ?: ""
                )
                val jsonAccessToken =
                    EncSharedPreferences.convertTestClassToJsonString(accessToken)
                Utility.getInstance()?.applicationContext?.let {
                    EncSharedPreferences.saveToEncryptedSharedPrefsString(
                        MainActivity.keyStringAccesskey, jsonAccessToken,
                        it
                    )
                }

            }
            override fun onFailure(call: Call<AccessToken>, t: Throwable) {
            }
        })
    }

    private fun getUserAndListAccountsFromNetwork() {
        UserNetwork.getUser { data ->
            Repository.userId = data.id.toString()
            Repository.userName = data.name.toString()

            ListAccountsNetwork.getAccounts {
                Repository.accounts = it as MutableList<ListAccounts.Data>
                val intent = Intent(requireContext(),MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}




