package com.example.cryptowallet

import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.classesapi.SendMoney
import com.squareup.moshi.Json


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
    var sendMoneyAddress=""
    var sendMoneyCurrency=""
    var sendMoneyAccountId=""
    var repoSendMoneyResponseCode =0



    //details for send money confirm
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

    var didntRequiredTwoFA = false

    var confirmAmount = ""
    var confirmCreatedAt = ""
    var confirmDescription = ""
    var confirmDetails =""
    var confirmId= ""
    var confirmNativeAmount =""
    var confirmNetwork= ""
    var confirmResource= ""
    var confirmResourcePath= ""
    var confirmStatus=""
    var confirmTo= ""
    var confirmType=""
    var confirmUpdatedAt= ""



}