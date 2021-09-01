package com.example.cryptowallet.adapter

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

class WalletSendAdapter(
    val onCLickSetId:(ListAccounts.Data) -> Unit
): ListAdapter<ListAccounts.Data, WalletSendAdapter.WalletsSendViewHolder>(diff) {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletsSendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWalletsBinding.inflate(inflater, parent, false)
        return WalletsSendViewHolder(binding, onCLickSetId)
    }
    override fun onBindViewHolder(holder: WalletsSendViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener { onCLickSetId(getItem(position)) }
        val currentWalletCurrency = getItem(position).balance?.currency
        if (currentWalletCurrency != null) {
            setIcon(currentWalletCurrency,holder)
        }
    }
    class WalletsSendViewHolder(
        private val binding: ItemWalletsBinding,
        private val onCLickForDetails: (ListAccounts.Data) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(wallet: ListAccounts.Data) {
            binding.apply {
                walletNameText.text = wallet.name
                walletIdText.text = "Balance: ${wallet.balance?.amount}"
                walletCurrencyText.text = "Currency: ${wallet.balance?.currency}"
            }
        }
    }
    private fun setIcon(currency: String, holder:WalletsSendViewHolder){
        Utility.getInstance()?.applicationContext?.let {
            Glide.with(it)
                .load("https://api.coinicons.net/icon/$currency/128x128")
                .into(holder.itemView
                    .findViewById<ImageView>(R.id.wallet_icon_image_view))
        }
    }
}