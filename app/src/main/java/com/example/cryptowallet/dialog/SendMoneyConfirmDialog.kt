package com.example.cryptowallet.dialog

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentSendMoneyConfirmDialogBinding.inflate(inflater)
        if(!Repository.didntRequiredTwoFA){
        runBlocking {
            SendMoney2FANetwork.sendMoney {
                binding.apply {
                    sendConfirmDialogY.text = it.toString()
                }
            }
        }
        }else{
            Repository.didntRequiredTwoFA = false
            binding.apply{
                sendConfirmDialogY.text = Repository.sendMoneyDataObj.toString()
            }
        }

        return MaterialAlertDialogBuilder(
            requireContext(), R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(binding.root)
            .setPositiveButton("Send") { _, _ ->
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

}