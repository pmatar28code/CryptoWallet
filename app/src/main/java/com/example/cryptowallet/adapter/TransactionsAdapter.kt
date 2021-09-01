package com.example.cryptowallet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptowallet.databinding.ItemTransactionsBinding
import com.example.cryptowallet.network.classesapi.ListTransactions

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
        val binding = ItemTransactionsBinding.inflate(inflater, parent, false)
        return TransactionsViewHolder(binding, onCLickSetId)
    }
    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener { onCLickSetId(getItem(position)) }
    }
    class TransactionsViewHolder(
        private val binding: ItemTransactionsBinding,
        private val onCLickForDetails: (ListTransactions.Data) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(transaction: ListTransactions.Data) {
            binding.apply {
                transactionIdText.text = "ID: ${transaction.id.toString()}"
                val amount = "Amount: ${transaction.amount?.amount.toString()} / "
                val currency = transaction.amount?.currency.toString()
                transactionAmountCurrencyText.text =  amount + currency
                transactionStatusText.text = "Status: ${transaction.status.toString()}"
                val nativeAmount = "Native Amount: ${transaction.nativeAmount?.amount.toString()} / "
                val nativeCurrency = transaction.nativeAmount?.currency.toString()
                if(transaction.amount?.amount.toString().contains("-")){
                    transactionTypeText.text = "Transaction: Debited/Sent"//transaction.type.toString()
                }else{
                    transactionTypeText.text = "Transaction: Deposit/Received"
                }
                transactionNativeAmountCurrencyText.text = nativeAmount + nativeCurrency
                transactionDateTimeText.text = "Created at: ${transaction.createdAt.toString()}"
            }
        }
    }
}