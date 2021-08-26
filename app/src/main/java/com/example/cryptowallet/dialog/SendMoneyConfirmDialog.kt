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
import kotlinx.coroutines.runBlocking

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
        runBlocking {
            SendMoney2FANetwork.sendMoney { sendMoneyData ->
                binding.apply {
                    if (sendMoneyData.id == "0") {
                        sendConfirmId.text = "Transaction could not be completed"
                    } else {
                        sendConfirmId.text = "Transation Successful, you will receive an email with confirmation. \n " +
                                "Keep in mind, transactions may take between 30 minutes and one hour to complete."
                    }
                }
            }
        }
        }else{
            Repository.didntRequiredTwoFA = false
                binding.apply {
                    if (Repository.sendMoneyDataObj.id == null) {
                        sendConfirmId.text = "Transaction could not be completed"
                    } else {
                        sendConfirmId.text = "Transation Successful, you will recieve an email with confirmation. \n " +
                                "Keep in mind, transactions may take between 30 minutes and one hour to complete."//it[lastIndex].id.toString()
                    }
                }
        }

        return MaterialAlertDialogBuilder(
            requireContext(), R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(binding.root)
            .setPositiveButton("Accept") { _, _ ->
            }
            .create()
    }
}