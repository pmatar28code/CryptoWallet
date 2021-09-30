package com.example.cryptowallet.adapter

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptowallet.R
import com.example.cryptowallet.databinding.ItemTransactionsBinding
import com.example.cryptowallet.network.classesapi.ListTransactions
import com.example.cryptowallet.utilities.setStringFromStringResources

class TransactionsAdapter(
    val resources: Resources,
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
        return TransactionsViewHolder(resources,binding, onCLickSetId)
    }
    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener { onCLickSetId(getItem(position)) }
    }
    class TransactionsViewHolder(
        private val resources: Resources,
        private val binding: ItemTransactionsBinding,
        private val onCLickForDetails: (ListTransactions.Data) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(transaction: ListTransactions.Data) {
            binding.apply {
                transactionIdText.text = this@TransactionsViewHolder.setStringFromStringResources(
                    resources,
                    R.string.transaction_adapter_transaction_id_text,
                    transaction.id.toString()
                )
                val amount = this@TransactionsViewHolder.setStringFromStringResources(
                    resources,
                    R.string.transaction_adapter_amount,
                    transaction.amount?.amount.toString()
                )
                val currency = transaction.amount?.currency.toString()
                transactionAmountCurrencyText.text =  amount + currency
                transactionStatusText.text = this@TransactionsViewHolder.setStringFromStringResources(
                    resources,
                    R.string.transaction_adapter_transaction_status_text,
                    transaction.status.toString()
                )
                val nativeAmount = this@TransactionsViewHolder.setStringFromStringResources(
                    resources,
                    R.string.transaction_adapter_native_amount,
                    transaction.nativeAmount?.amount.toString()
                )
                val nativeCurrency = transaction.nativeAmount?.currency.toString()

                if(transaction.amount?.amount.toString().contains("-")){
                    transactionTypeText.text = resources.getString(
                        R.string.transaction_adapter_transaction_type_sent_debited
                    )
                }else{
                    transactionTypeText.text = resources.getString(
                        R.string.transaction_adapter_transaction_type_received_deposit
                    )
                }
                transactionNativeAmountCurrencyText.text = nativeAmount + nativeCurrency
                transactionDateTimeText.text = this@TransactionsViewHolder.setStringFromStringResources(
                    resources,
                    R.string.transaction_adapter_transaction_day_time_text,
                    transaction.createdAt.toString()
                )
            }
        }
    }
}