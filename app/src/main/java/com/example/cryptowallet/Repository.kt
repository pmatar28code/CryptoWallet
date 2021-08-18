package com.example.cryptowallet

import android.accounts.Account
import com.example.cryptowallet.network.classesapi.ListAccounts


object Repository {
    var userId = ""
    var userName = ""
    var accounts = mutableListOf<ListAccounts.Data>()
    var accountId = ""
    var token2fa =""
    var currency=""
}