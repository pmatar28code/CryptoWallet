package com.example.cryptowallet.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.cryptowallet.R
import com.example.cryptowallet.databinding.FragmentTransactionDetailsBinding
import com.example.cryptowallet.network.networkcalls.ListTransactionsNetwork
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TransactionsDetailDialog:DialogFragment() {
    companion object {
        fun create(listener: () -> Unit): TransactionsDetailDialog {
            return TransactionsDetailDialog().apply {

            }
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentTransactionDetailsBinding.inflate(inflater)

        binding.apply {
            ListTransactionsNetwork.getTransactions {
                var lastIndex = it.lastIndex
                transactionId.text = it[lastIndex].id
                transactionAmountCurrency.text = "${it[lastIndex].amount?.amount} / ${it[lastIndex].amount?.currency}"
                transactionStatus.text = it[lastIndex].status
                transactionNativeAmountCurrency.text = "${it[lastIndex].nativeAmount?.amount} / ${it[lastIndex].nativeAmount?.currency}"
                transactionCreated.text = it[lastIndex].createdAt
            }
        }


        return MaterialAlertDialogBuilder(
            requireContext(),R.style.MyRounded_MaterialComponents_MaterialAlertDialog)
            .setView(binding.root)
            .setPositiveButton(getString(R.string.accept_button)){_,_ ->

            }
            .create()
    }
}