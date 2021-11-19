package com.example.cryptowallet.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.cryptowallet.BuildConfig
import com.example.cryptowallet.MainActivity
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentAuthorizationBinding
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.GetTokenNetwork
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.network.networkcalls.UserNetwork
import com.example.cryptowallet.utilities.EncSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthorizationFragment: Fragment(R.layout.fragment_authorization) {
    companion object {
        private const val MY_CLIENT_ID = BuildConfig.MY_CLIENT_ID
        var code: String = ""
    }
    @Inject
    lateinit var listAccountsNetwork: ListAccountsNetwork
    @Inject lateinit var userNetwork: UserNetwork
    @Inject lateinit var getTokenNetwork: GetTokenNetwork

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAuthorizationBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        val urlString = String.format(
            resources.getString(
                R.string.url_string
            ), MY_CLIENT_ID
        )

        val webView: WebView = binding.webViewAuthorization
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                code = request.url.toString()
                if (code.contains("?") && code.length == 106) {
                    code = code.removeRange(0, 41)
                    code = code.dropLast(1)
                    EncSharedPreferences.saveToEncryptedSharedPrefsString(
                        "Auth_code", code,
                        requireContext()
                    )
                    getTokenNetwork.getFirstToken {
                        Repository.tempAccessToken = it
                        val jsonAccessToken =
                            EncSharedPreferences.convertTestClassToJsonString(it)

                        EncSharedPreferences.saveToEncryptedSharedPrefsString(
                            MainActivity.keyStringAccesskey, jsonAccessToken,
                            requireContext()
                        )
                        getUserAndListAccountsFromNetwork()
                    }

                    return true
                }
                return false
            }
        }
        webView.loadUrl(urlString)
    }
    private fun getUserAndListAccountsFromNetwork() {
        userNetwork.getUser { data ->
            Repository.userId = data.id.toString()
            Repository.userName = data.name.toString()

            listAccountsNetwork.getAccounts {
                Repository.accounts = it as MutableList<ListAccounts.Data>
                val intent = Intent(requireContext(),MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}




