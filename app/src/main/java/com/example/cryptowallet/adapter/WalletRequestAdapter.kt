package com.example.cryptowallet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptowallet.R
import com.example.cryptowallet.databinding.ItemWalletsBinding
import com.example.cryptowallet.network.classesapi.ListAccounts
import java.util.*

class WalletRequestAdapter(
    val onCLickSetId:(ListAccounts.Data) -> Unit
): ListAdapter<ListAccounts.Data, WalletRequestAdapter.WalletsRequestViewHolder>(diff) {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletsRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWalletsBinding.inflate(inflater, parent, false)
        return WalletsRequestViewHolder(binding, onCLickSetId)
    }
    override fun onBindViewHolder(holder: WalletsRequestViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener { onCLickSetId(getItem(position)) }
        val context = holder.itemView.context
        val currentWalletCurrency = getItem(position).balance?.currency
        if (currentWalletCurrency != null) {
            setIcon(context,currentWalletCurrency,holder)
        }
    }
    class WalletsRequestViewHolder(
        private val binding: ItemWalletsBinding,
        private val onCLickForDetails: (ListAccounts.Data) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(wallet: ListAccounts.Data) {
            binding.apply {
                walletNameText.text = wallet.name
                walletIdText.text = "Balance: ${wallet.balance?.amount}"
                walletCurrencyText.text = "Currency: ${wallet.balance?.currency}"
            }
        }
    }
    private fun setIcon(context:Context,currency: String, holder:WalletsRequestViewHolder){
        val currencyToLowercase = currency.lowercase(Locale.getDefault())
        Glide.with(context)
            .load("https://cryptoicon-api.vercel.app/api/icon/$currencyToLowercase")
            .into(holder.itemView
                .findViewById(R.id.wallet_icon_image_view))
    }
}