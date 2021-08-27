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
import com.example.cryptowallet.network.classesapi.ListTransactions
import com.example.cryptowallet.utilities.Utility

class TransactionsAdapter(
    val onCLickSetId:(ListTransactions.Data) -> Unit
): ListAdapter<ListTransactions.Data, TransactionsAdapter.TransactionsViewHolder>(diff) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<ListTransactions.Data>() {
            override fun areItemsTheSame(
                oldItem: ListTransactions.Data,
                newItem: ListTransactions.Data
            ): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: ListTransactions.Data,
                newItem: ListTransactions.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWalletsBinding.inflate(inflater, parent, false)
        return TransactionsViewHolder(binding, onCLickSetId)
    }
    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener { onCLickSetId(getItem(position)) }
        //val currentWalletCurrency = getItem(position).balance?.currency
       // if (currentWalletCurrency != null) {
         //   setIcon(currentWalletCurrency,holder)
        //}
    }
    class TransactionsViewHolder(
        private val binding: ItemWalletsBinding,
        private val onCLickForDetails: (ListTransactions.Data) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(transaction: ListTransactions.Data) {
            binding.apply {
                walletNameText.text = transaction.id.toString()
                walletIdText.text =  "${transaction.amount?.amount.toString()} / ${transaction.amount?.currency.toString()}"
                walletCurrencyText.text = transaction.status.toString()
            }
        }
    }
    private fun setIcon(currency: String, holder:TransactionsViewHolder){
        Utility.getInstance()?.applicationContext?.let {
            Glide.with(it)
                .load("https://api.coinicons.net/icon/$currency/128x128")
                .into(holder.itemView
                    .findViewById<ImageView>(R.id.wallet_icon_image_view))
        }
    }
}