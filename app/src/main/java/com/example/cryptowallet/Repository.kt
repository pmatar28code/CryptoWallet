package com.example.cryptowallet

import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.classesapi.SendMoney

object Repository {
    var userId = ""
    var userName = ""
    var accounts = mutableListOf<ListAccounts.Data>()
    var accountId = ""
    var token2fa =""
    var currency=""
    var address =""
    var iconAddress =""
    var sendMoneyAmount =""
    var sendMonetTo=""
    var sendMoneyCurrency=""
    var sendMoneyAccountId=""
    var repoSendMoneyResponseCode =0
    var walletDetailsAccountId =""
    var sendMoneyDataObj = SendMoney.Data(
        amount = SendMoney.Data.Amount("",""),
        createdAt = "",
        description = "",
        details = SendMoney.Data.Details("",""),
        id= "",
        nativeAmount = SendMoney.Data.NativeAmount("",""),
        network= SendMoney.Data.Network("","",""),
        resource= "",
        resourcePath= "",
        status="",
        to= SendMoney.Data.To("",""),
        type="",
        updatedAt= ""
    )
    var setTransactionIdForSpecificNetworkRequest =""
    var setTransactionCurrencyForIcon=""
    var didntRequiredTwoFA = false
}