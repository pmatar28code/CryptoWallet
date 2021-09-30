package com.example.cryptowallet.utilities

import android.content.Context
import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptowallet.R
import com.example.cryptowallet.adapter.TransactionsAdapter

fun RecyclerView.ViewHolder.setIcon(context: Context, currency: String, holder: RecyclerView.ViewHolder){
    val currencyToLowercase = currency.lowercase(java.util.Locale.getDefault())
    com.bumptech.glide.Glide.with(context)
        .load("https://cryptoicon-api.vercel.app/api/icon/$currencyToLowercase")
        .into(holder.itemView
            .findViewById(com.example.cryptowallet.R.id.wallet_icon_image_view))
}

fun TransactionsAdapter.TransactionsViewHolder.setStringFromStringResources(
    resources: Resources,
    stringResource:Int,
    stringPlaceholder:String
):String{

    return String.format(resources.getString(stringResource),stringPlaceholder)
}