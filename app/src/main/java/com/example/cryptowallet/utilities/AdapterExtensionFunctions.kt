package com.example.cryptowallet.utilities

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.ViewHolder.setIcon(context: Context, currency: String, holder: RecyclerView.ViewHolder){
    val currencyToLowercase = currency.lowercase(java.util.Locale.getDefault())
    com.bumptech.glide.Glide.with(context)
        .load("https://cryptoicon-api.vercel.app/api/icon/$currencyToLowercase")
        .into(holder.itemView
            .findViewById(com.example.cryptowallet.R.id.wallet_icon_image_view))
}