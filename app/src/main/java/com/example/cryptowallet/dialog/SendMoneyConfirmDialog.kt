package com.example.cryptowallet.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentSendMoneyConfirmDialogBinding
import com.example.cryptowallet.network.networkcalls.SendMoney2FANetwork
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SendMoneyConfirmDialog: DialogFragment() {
    companion object {
        fun create(listener: () -> Unit): SendMoneyConfirmDialog {
            return SendMoneyConfirmDialog().apply {

            }
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentSendMoneyConfirmDialogBinding.inflate(inflater)
        if(!Repository.didntRequiredTwoFA){
            SendMoney2FANetwork.sendMoney { sendMoneyData ->
                binding.apply {
                    if (sendMoneyData.id == "0") {
                        sendConfirmId.text = getString(R.string.send_confirm_dialog_transaction_not_completed)
                    } else {
                        sendConfirmId.text = getString(R.string.send_confirm_dialog_transaction_successful)
                    }
                }
            }
        }else{
            Repository.didntRequiredTwoFA = false
                binding.apply {
                    if (Repository.sendMoneyDataObj.id == null) {
                        sendConfirmId.text = getString(R.string.send_confirm_dialog_transaction_not_completed)
                    } else {
                        sendConfirmId.text = getString(R.string.send_confirm_dialog_transaction_successful)
                    }
                }
        }

        return MaterialAlertDialogBuilder(
            requireContext(), R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(binding.root)
            .setPositiveButton(getString(R.string.accept_button)) { _, _ ->
            }
            .create()
    }
}