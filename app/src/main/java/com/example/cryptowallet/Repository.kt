package com.example.cryptowallet

import android.content.Context
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

    fun setIcon(context: Context, currency: String, holder: RecyclerView.ViewHolder){
        val currencyToLowercase = currency.lowercase(Locale.getDefault())
        Glide.with(context)
            .load("https://cryptoicon-api.vercel.app/api/icon/$currencyToLowercase")
            .into(holder.itemView
                .findViewById(R.id.wallet_icon_image_view))
    }

    fun glideForRequestMoneyDialog(
        binding: FragmentRequestMoneyDialogBinding,
        context:Context
    ) {
        binding.apply {
            Glide.with(context)
                .load(
                    "https://cryptoicon-api.vercel.app/api/icon/${
                        currency.lowercase(
                            Locale.getDefault()
                        )
                    }"
                )
                .into(requestDialogIcon)
            requestDialogAddressText.text = address
            requestDialogTitleText.text = currency
            val urlForQr =
                "http://api.qrserver.com/v1/create-qr-code/?data=$address&size=1600x1600"
            Glide.with(context)
                .load(urlForQr)
                .into(requestDialogQrcodeImage)
        }
    }
}