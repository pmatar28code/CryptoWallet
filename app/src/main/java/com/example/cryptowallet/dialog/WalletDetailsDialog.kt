package com.example.cryptowallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentSendMoneyConfirmDialogBinding
import com.example.cryptowallet.databinding.FragmentWalletDetailsBinding
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.network.networkcalls.SendMoney2FANetwork
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.runBlocking

class WalletDetailsDialog: DialogFragment() {
    companion object {
        fun create(listener: () -> Unit): WalletDetailsDialog {
            return WalletDetailsDialog().apply {

            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentWalletDetailsBinding.inflate(inflater)

        ListAccountsNetwork.getAccounts { ListOfAccounts ->
            for(wallet in ListOfAccounts){
                if(wallet.id == Repository.walletDetailsAccountId){
                    binding.walletDetailsNameText.text = wallet.name
                    binding.walletDetailsBalanceAmount.text = wallet.balance?.amount
                    binding.walletDetailsCurrency.text = wallet.balance?.currency
                }
            }
        }

        return MaterialAlertDialogBuilder(
            requireContext(), R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(binding.root)
            .setPositiveButton("Close") { _, _ ->
            }

            .create()
    }

}