package com.example.cryptowallet.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.cryptowallet.MainActivity
import com.example.cryptowallet.R
import com.example.cryptowallet.databinding.FragmentAuthorizationBinding
import com.example.cryptowallet.utilities.EncSharedPreferences
import com.example.cryptowallet.utilities.Utility


class AuthorizationFragment: Fragment(R.layout.fragment_authorization) {
    companion object{
        const val MY_CLIENT_ID = "c77416def5b58698219596f44ecf6236658c426805a522d517f45867b0348188"
        val urlString = "https://www.coinbase.com/oauth/authorize?client_id=$MY_CLIENT_ID&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&account=all&scope=wallet:accounts:read wallet:addresses:create wallet:addresses:read wallet:accounts:update wallet:accounts:create wallet:transactions:send wallet:transactions:request&meta[send_limit_amount]=1&meta[send_limit_currency]=USD&meta[send_limit_period]=day"

        // lateinit var browser : WebView
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAuthorizationBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
/*
        // find the WebView by name in the main.xml of step 2
        browser = binding.webViewAuthorization

        // Enable javascript
        browser.settings.javaScriptEnabled = true

        // Set WebView client
        //browser.setWebChromeClient( WebChromeClient());
        val url =
            "https://www.coinbase.com/oauth/authorize?client_id=$MY_CLIENT_ID&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&response_type=code&account=all&scope=wallet%3Aaccounts%3Aread+wallet%3Aaddresses%3Acreate+wallet%3Aaddresses%3Aread+wallet%3Aaccounts%3Aupdate+wallet%3Aaccounts%3Acreate+wallet%3Atransactions%3Asend+wallet%3Atransactions%3Arequest&meta[send_limit_amount]=1&meta[send_limit_currency]=USD&meta[send_limit_period]=day"
        //browser.webChromeClient = WebChromeClient()
        val urlString = "https://www.coinbase.com/oauth/authorize?client_id=$MY_CLIENT_ID&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&account=all&scope=wallet:accounts:read wallet:addresses:create wallet:addresses:read wallet:accounts:update wallet:accounts:create wallet:transactions:send wallet:transactions:request&meta[send_limit_amount]=1&meta[send_limit_currency]=USD&meta[send_limit_period]=day"
        browser.webViewClient = WebViewClient()
        //browser.loadUrl(urlString)
        */

        val webView: WebView = binding.webViewAuthorization
        //webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true

       // webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

       // val urlString = "https://www.coinbase.com/oauth/authorize?client_id=$MY_CLIENT_ID&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&account=all&scope=wallet:accounts:read wallet:addresses:create wallet:addresses:read wallet:accounts:update wallet:accounts:create wallet:transactions:send wallet:transactions:request&meta[send_limit_amount]=1&meta[send_limit_currency]=USD&meta[send_limit_period]=day"

        //webView.loadUrl(urlString)


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                var code = request.url.toString()
                Log.e("TST OVERRIDE", "THIS OVERRIDE $code")
                if (code.contains("?")) {
                    code = code.removeRange(0,41)
                    code = code.dropLast(1)
                    Utility.getInstance()?.applicationContext?.let {
                        EncSharedPreferences.saveToEncryptedSharedPrefsString("Auth_code",code,
                            it
                        )
                    }

                    var intent = Intent(requireContext(), MainActivity::class.java)
                    //intent.putExtra("url_code", test)
                    startActivity(intent)
                    //view.loadUrl(urlString)
                    return true
                }
                return false
            }

        }
        webView.loadUrl(urlString)




    }


}




