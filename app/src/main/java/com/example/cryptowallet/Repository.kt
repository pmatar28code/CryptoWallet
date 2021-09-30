package com.example.cryptowallet

import android.content.Context
import android.content.res.Resources
import android.renderscript.ScriptGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptowallet.databinding.FragmentRequestMoneyDialogBinding
import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.classesapi.SendMoney
import java.util.*

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
    var tempAccessToken : AccessToken?= null

    fun glideForRequestMoneyDialog(
        resources: Resources,
        binding: FragmentRequestMoneyDialogBinding,
        context:Context
    ) {
        val iconAddress = String.format(
            resources.getString(
                R.string.repository_icon_address
            ), currency.lowercase(Locale.getDefault())
        )
        binding.apply {
            Glide.with(context)
                .load(iconAddress)
                .into(requestDialogIcon)
            requestDialogAddressText.text = address
            requestDialogTitleText.text = currency
            val urlForQr = String.format(
                resources.getString(
                    R.string.repository_url_for_qr
                ),address
            )
            Glide.with(context)
                .load(urlForQr)
                .into(requestDialogQrcodeImage)
        }
    }
}