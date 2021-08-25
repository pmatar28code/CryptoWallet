package com.example.cryptowallet.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.isVisible
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
            SendMoney2FANetwork.sendMoney {
                binding.apply {
                    if(it.id == "0"){
                        sendConfirmId.text = "Transaction could not be completed"
                        sendConfirmAmountCurrency.isVisible =false
                        sendConfirmStatus.isVisible = false
                        sendConfirmCreated.isVisible = false
                        binding.sendConfirmNativeAmountCurrency.isVisible = false
                    }else {
                        sendConfirmId.text = it.id
                        sendConfirmAmountCurrency.text = "${it.amount?.amount} / ${it.amount?.currency}"
                        sendConfirmStatus.text = "Status: ${it.status}"
                        sendConfirmCreated.text = "Created at: ${it.createdAt}"
                        sendConfirmNativeAmountCurrency.text = "Native: ${it.nativeAmount?.amount} / ${it.nativeAmount?.currency}"
                    }
                }
            }
        }
        }else{
            Repository.didntRequiredTwoFA = false
            binding.apply{
                //sendConfirmDialogY.text = Repository.sendMoneyDataObj.toString()
                sendConfirmId.text = Repository.sendMoneyDataObj.id
                sendConfirmAmountCurrency.text = "${Repository.sendMoneyDataObj.amount?.amount} / ${Repository.sendMoneyDataObj.amount?.currency}"
                sendConfirmStatus.text = "Status: ${Repository.sendMoneyDataObj.status}"
                sendConfirmCreated.text = "Created at: ${Repository.sendMoneyDataObj.createdAt}"
                sendConfirmNativeAmountCurrency.text = "Native: ${Repository.sendMoneyDataObj.nativeAmount?.amount} / ${Repository.sendMoneyDataObj.nativeAmount?.currency}"
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