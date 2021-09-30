package com.example.cryptowallet.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.cryptowallet.MainActivity
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentSendMoneyConfirmDialogBinding
import com.example.cryptowallet.network.networkcalls.SendMoney2FANetwork
import com.example.cryptowallet.utilities.EncSharedPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SendMoneyConfirmDialog: DialogFragment() {
    companion object {
        fun create(listener: () -> Unit): SendMoneyConfirmDialog {
            return SendMoneyConfirmDialog().apply {

            }
        }
    }
    @Inject
    lateinit var sendMoney2FANetwork: SendMoney2FANetwork
    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentSendMoneyConfirmDialogBinding.inflate(inflater)
        if(!Repository.didntRequiredTwoFA){
            sendMoney2FANetwork.sendMoney { sendMoneyData ->
                storeMostRecentTokenInEncSharedPreferences()
                binding.apply {
                    sendConfirmId.text = if (sendMoneyData.id == "0") {
                        getString(R.string.send_confirm_dialog_transaction_not_completed)
                    } else {
                        getString(R.string.send_confirm_dialog_transaction_successful)
                    }
                }
            }
        }else{
            Repository.didntRequiredTwoFA = false
                binding.apply {
                    sendConfirmId.text = if (Repository.sendMoneyDataObj.id == null) {
                        getString(R.string.send_confirm_dialog_transaction_not_completed)
                    } else {
                        getString(R.string.send_confirm_dialog_transaction_successful)
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

    private fun storeMostRecentTokenInEncSharedPreferences(){
        val stringAccessToken = Repository.tempAccessToken?.let { accessToken ->
            EncSharedPreferences.convertTestClassToJsonString(
                accessToken
            )
        }
        if (stringAccessToken != null) {
            EncSharedPreferences.saveToEncryptedSharedPrefsString(MainActivity.keyStringAccesskey,stringAccessToken,requireContext())
        }
    }
}