package com.example.cryptowallet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptowallet.R
import com.example.cryptowallet.databinding.ItemWalletsBinding
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.utilities.Utility

class WalletsAdapter(
    val onCLickSetId:(ListAccounts.Data) -> Unit
): ListAdapter<ListAccounts.Data, WalletsAdapter.WalletsViewHolder>(diff) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<ListAccounts.Data>() {
            override fun areItemsTheSame(
                oldItem: ListAccounts.Data,
                newItem: ListAccounts.Data
            ): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: ListAccounts.Data,
                newItem: ListAccounts.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWalletsBinding.inflate(inflater, parent, false)
        return WalletsViewHolder(binding, onCLickSetId)
    }
    override fun onBindViewHolder(holder: WalletsViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener { onCLickSetId(getItem(position)) }
        val currentWalletCurrency = getItem(position).balance?.currency
        if (currentWalletCurrency != null) {
            setIcon(currentWalletCurrency,holder)
        }
    }
    class WalletsViewHolder(
        private val binding: ItemWalletsBinding,
        private val onCLickForDetails: (ListAccounts.Data) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(wallet: ListAccounts.Data) {
            binding.apply {
                walletNameText.text = wallet.name
                walletIdText.text = "ID:${wallet.id}"
                walletCurrencyText.text = "Currency: ${wallet.balance?.currency}"
            }
        }
    }
    private fun setIcon(currency: String, holder:WalletsViewHolder){
        Utility.getInstance()?.applicationContext?.let {
            Glide.with(it)
                .load("https://api.coinicons.net/icon/$currency/128x128")
                .into(holder.itemView
                    .findViewById<ImageView>(R.id.wallet_icon_image_view))
        }
    }
}