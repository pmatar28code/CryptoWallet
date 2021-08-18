package com.example.cryptowallet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.example.cryptowallet.databinding.ActivityMainBinding
import com.example.cryptowallet.fragments.RequestFragment
import com.example.cryptowallet.fragments.SendFragment
import com.example.cryptowallet.fragments.WalletFragment
import com.example.cryptowallet.network.apis.CoinBaseClient
import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.network.classesapi.CompleteRequestMoneyApi
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.*
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.example.cryptowallet.oauth.TokenAuthorizationInterceptor
import com.example.cryptowallet.oauth.TokenRefreshAuthenticatorCoinBase
import com.example.cryptowallet.utilities.EncSharedPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    companion object {
        const val MY_CLIENT_ID = "c77416def5b58698219596f44ecf6236658c426805a522d517f45867b0348188"
        const val CLIENT_SECRET = "311b687baee92bbd8e584527bb757f27c9ed363f7a3922a18b381bcd4309b5b4"
        const val MY_REDIRECT_URI = "cryptowallet://callback"
        const val keyStringAccesskey = "Access_key"
        const val keyStringCode = "Auth_code"
        var codeFromShared:String ?= null
        var stringTokenFromShared:String ?= null
        var accessTokenFromShared:AccessToken ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        runBlocking {
            val job:Job = launch(IO) {
                codeFromShared = EncSharedPreferences.getValueString(keyStringCode,applicationContext)
                Log.e("HOW CODE VALUE STRING LOOKS MAIN:",codeFromShared?:"not looking good")
                stringTokenFromShared = EncSharedPreferences.getValueString(keyStringAccesskey,applicationContext)
                joinAll()
            }
        }
        if (codeFromShared == null) {
            val url = "https://www.coinbase.com/oauth/authorize?client_id=$MY_CLIENT_ID&redirect_uri=cryptowallet%3A%2F%2Fcallback&response_type=code&account=all&scope=wallet%3Aaccounts%3Aread+wallet%3Aaddresses%3Acreate+wallet%3Aaddresses%3Aread+wallet%3Aaccounts%3Aupdate+wallet%3Aaccounts%3Acreate+wallet%3Atransactions%3Asend+wallet%3Atransactions%3Arequest&meta[send_limit_amount]=1&meta[send_limit_currency]=USD&meta[send_limit_period]=day"
            val customTabsIntentBuilder  =  CustomTabsIntent.Builder()
            val customTabsIntent = customTabsIntentBuilder.build()
            //customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            //customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            //customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            //customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            //customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

            customTabsIntent.launchUrl(this, Uri.parse(url))

/*
            val intent = Intent(
               Intent.ACTION_VIEW,
                Uri.parse("https://www.coinbase.com/oauth/authorize?client_id=$MY_CLIENT_ID&redirect_uri=cryptowallet%3A%2F%2Fcallback&response_type=code&account=all&scope=wallet%3Aaccounts%3Aread+wallet%3Aaddresses%3Acreate+wallet%3Aaddresses%3Aread+wallet%3Aaccounts%3Aupdate+wallet%3Aaccounts%3Acreate+wallet%3Atransactions%3Asend+wallet%3Atransactions%3Arequest&meta[send_limit_amount]=1&meta[send_limit_currency]=USD&meta[send_limit_period]=day")
            )
            startActivity(intent)
            Log.e("FIRST Run", "getting the code")*/
        } else {
            val jsonStringAccessToken = EncSharedPreferences.getValueString(keyStringAccesskey,applicationContext)?:""
            accessTokenFromShared = EncSharedPreferences.convertJsonStringToTestClass(jsonStringAccessToken)
            //Repository.accessToken = EncSharedPreferences.convertJsonStringToTestClass(jsonStringAccessToken)
            Log.e(
                "WHATS NEXT",
                "DO API Requests WITH TOKEN AVAILABLE CODE:$codeFromShared, Token:${
                    accessTokenFromShared?.access_token
                }"
            )
            UserNetwork.getUser {
                runBlocking {
                    var job:Job = launch(IO) {
                        Log.e("SHOWING USER", "${it.name}, id: ${it.id} WITH TOKEN = ${accessTokenFromShared?.access_token}}")
                        Repository.userId = it.id.toString()
                        Repository.userName = it.name.toString()
                    }
                }
            }
            ListAccountsNetwork.getAccounts {
                Repository.accounts = it as MutableList<ListAccounts.Data>
                runBlocking {
                    var job:Job = launch(IO) {
                        Log.e(
                            "LIST OF ACCOUNTS MAIN OJO: ",
                            "ID: ${it[0].id}, ${it[0].name},type = ${it[0].type},primary = ${it[0].primary}, ${it[0].balance}, ${it[0].currency} WITH TOKEN = ${accessTokenFromShared?.access_token}"
                        )
                        Log.e("ALL THE LIST OFF ACCOUNTS MAIN:","$it")
                        swapFragments(WalletFragment())
                    }
                }
            }
            binding.apply {
                bottomNavigationContainer.setOnNavigationItemSelectedListener {
                    handleBottomNavigation(it.itemId, binding)
                }
            }
            //swap fragment to wallet which is going to be the main one to show to the user
            //listing accounts that have balance > 0
            swapFragments(WalletFragment())
            /*
            example complete money request after request money successfull which it havent been 'type stuff
            RequestMoneyNetwork.getRequestMoney {
                Log.e("REQUEST MONEY MAIN: ","To: ${it.to}, amount: ${it.amount?.amount}, currency: ${it.amount?.currency} ")
                val logger = HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY )
                val client = OkHttpClient.Builder()
                    .addNetworkInterceptor(TokenAuthorizationInterceptor(AccessTokenProviderImp()))
                    //.addInterceptor(logger)
                    .authenticator(TokenRefreshAuthenticatorCoinBase(AccessTokenProviderImp()))
                    .build()
                val retrofitBuilder = Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://api.coinbase.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                val retrofit = retrofitBuilder.build()
                val completeMoneyRequestClient = retrofit.create(CompleteRequestMoneyApi::class.java)
                val completeMoneyRequestCall = completeMoneyRequestClient.completeRequestMoney(
                    "authorization_code",
                    it.id?:""
                )
                completeMoneyRequestCall.enqueue(object: Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.e("ON RESPONSE COMPLETE REQUEST MAIN: ","success? ${response.isSuccessful}, message: ${response.message()}")
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("COMPLETE REQUEST MAIN ON FAILURE:","$t")
                    }

                })
                */
            }
        }
    override fun onResume() {
        super.onResume()
        val uri = intent.data
        if(uri != null){
            val code = uri.getQueryParameter("code")!!
            CoroutineScope(IO).launch {
                EncSharedPreferences.saveToEncryptedSharedPrefsString(keyStringCode,code,applicationContext)
                val testCodeList2 = EncSharedPreferences.getValueString(keyStringCode,applicationContext)
                Log.e("ADDING CODE","ADDED CODE TO DATABASE $testCodeList2")
            }
            val retrofitBuilder = Retrofit.Builder()
                .baseUrl("https://api.coinbase.com/")
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = retrofitBuilder.build()
            val coinBaseClient = retrofit.create(CoinBaseClient::class.java)
            val accessTokenCall = coinBaseClient.getToken(
                "authorization_code",
                code,
                MY_CLIENT_ID,
                CLIENT_SECRET,
                MY_REDIRECT_URI
            )
            accessTokenCall.enqueue(object: Callback<AccessToken> {
                override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                    Toast.makeText(this@MainActivity,"good response: ${response.body()?.access_token}",Toast.LENGTH_SHORT).show()
                    var accessToken = AccessToken(
                        access_token =  response.body()?.access_token?:"",
                        token_type =  response.body()?.token_type?:"",
                        expires_in = response.body()?.expires_in?:0,
                        refresh_token = response.body()?.refresh_token?:"",
                        scope =  response.body()?.scope?:""
                    )
                    if(accessToken != null) {
                        val jsonAccessToken = EncSharedPreferences.convertTestClassToJsonString(accessToken)
                        EncSharedPreferences.saveToEncryptedSharedPrefsString(keyStringAccesskey,jsonAccessToken,applicationContext)
                        Log.e("ADDED TOKEN TO DATABASE","ACCESS TOKEN ADDED TO EncSharedPrefs $accessToken")
                        val intent =Intent(this@MainActivity, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Log.e("TOKEN IS NULL","ACCESS TOKEN IS NULL $accessToken")
                    }

                }
                override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                    Toast.makeText(this@MainActivity,"bad response",Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    /*
    private fun revokeToken(){
        //changed to encsharedPreferences instead of database
        val databaseRevoke =  database?.AccessTokenDao()?.getAllTokens()?.get(0)?.refresh_token!!
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://api.coinbase.com/")
            .addConverterFactory(MoshiConverterFactory.create())
        val retrofit = retrofitBuilder.build()
        val revokeTokenClient = retrofit.create(RevokeTokenApi::class.java)
        val revokeCall = revokeTokenClient.revokeToken(
            databaseRevoke,
            "Bearer $databaseRevoke "
        )
        revokeCall.enqueue(object:Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.e("IS IT REVOKED MAIN ON RESPONSE","${response.body()}")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ON FAILURE REVOKE","$t")
            }

        })
    }
     */
    private fun handleBottomNavigation(
        menuItemId: Int, binding: ActivityMainBinding
    ): Boolean = when (menuItemId) {

        R.id.menu_wallet -> {
            swapFragments(WalletFragment())
            true
        }
        R.id.menu_request -> {
            Repository.accounts.clear()
            swapFragments(RequestFragment())
            true
        }
        R.id.menu_send -> {
            Repository.accounts.clear()
            swapFragments(SendFragment())
            true
        }
        else -> false
    }

    //override
    fun swapFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("back")
            .commit()
    }
}
