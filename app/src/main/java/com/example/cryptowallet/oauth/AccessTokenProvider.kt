package com.example.cryptowallet.oauth

import com.example.cryptowallet.network.classesapi.AccessToken

interface AccessTokenProvider {
    fun token(): AccessToken?
    fun refreshToken(refreshCallback: (Boolean) -> Unit)
}
