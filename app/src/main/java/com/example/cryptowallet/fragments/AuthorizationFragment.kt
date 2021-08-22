package com.example.cryptowallet.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cryptowallet.MainActivity
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentAuthorizationBinding
import com.example.cryptowallet.interfacex.MainInterface
import com.example.cryptowallet.network.apis.CoinBaseClient
import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.network.networkcalls.UserNetwork
import com.example.cryptowallet.utilities.EncSharedPreferences
import com.example.cryptowallet.utilities.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthorizationFragment: Fragment(R.layout.fragment_authorization) {
    companion object {
        const val MY_CLIENT_ID = "c77416def5b58698219596f44ecf6236658c426805a522d517f45867b0348188"
        val urlString = "https://www.coinbase.com/oauth/authorize?client_id=$MY_CLIENT_ID&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&account=all&scope=wallet:accounts:read wallet:addresses:create wallet:addresses:read wallet:accounts:update wallet:accounts:create wallet:transactions:send wallet:transactions:request&meta[send_limit_amount]=1&meta[send_limit_currency]=USD&meta[send_limit_period]=day"
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
                //Log.e("TST OVERRIDE", "THIS OVERRIDE $code")
                if (code.contains("?") && code.length == 106) {
                    code = code.removeRange(0, 41)
                    code = code.dropLast(1)
                    Log.e("TST OVERRIDE", "HOW CODE ENDED UP $code")
                    Utility.getInstance()?.applicationContext?.let {
                        EncSharedPreferences.saveToEncryptedSharedPrefsString(
                            "Auth_code", code,
                            it
                        )
                    }
                    getTokenNetworkRequest(code)
                    getUserAndListAccountsFromNetwork()
                    //val intent = Intent(requireContext(),MainActivity::class.java)
                    //startActivity(intent)
                    return true
                }
                return false
            }
        }
        webView.loadUrl(urlString)
    }

    private fun getTokenNetworkRequest(code: String) {
        runBlocking {
            val job: Job = launch {
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
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "good response: ${response.body()?.access_token}",
                            Toast.LENGTH_SHORT
                        ).show()
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
                        Log.e(
                            "ADDED TOKEN TO DATABASE",
                            "ACCESS TOKEN ADDED TO EncSharedPrefs $accessToken"
                        )

                        //intent
                    }

                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        Toast.makeText(requireContext(), "bad response", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
        }
    }

    private fun getUserAndListAccountsFromNetwork() {
        UserNetwork.getUser {
            runBlocking {
                var job: Job = launch(Dispatchers.IO) {
                    Log.e("SHOWING USER", "${it.name}, id: ${it.id} WITH TOKEN = ${MainActivity.accessTokenFromShared?.access_token}}"
                    )
                    Repository.userId = it.id.toString()
                    Repository.userName = it.name.toString()

                    ListAccountsNetwork.getAccounts {
                        Repository.accounts = it as MutableList<ListAccounts.Data>

                        Log.e(
                            "LIST OF ACCOUNTS MAIN OJO: ",
                            "ID: ${it[0].id}, ${it[0].name},type = ${it[0].type},primary = ${it[0].primary}, ${it[0].balance}, ${it[0].currency} WITH TOKEN = ${MainActivity.accessTokenFromShared?.access_token}"
                        )
                        Log.e("ALL THE LIST OFF ACCOUNTS MAIN:", "$it")
                        //swapFragments(WalletFragment())
                        val intent = Intent(requireContext(),MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}




