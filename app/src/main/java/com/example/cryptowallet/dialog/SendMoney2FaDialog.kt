package com.example.cryptowallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentRequestMoneyDialogBinding
import com.example.cryptowallet.databinding.FragmentSendMoney2faDialogBinding
import com.example.cryptowallet.network.classesapi.SendMoney
import com.example.cryptowallet.network.networkcalls.SendMoney2FANetwork
import com.example.cryptowallet.network.networkcalls.SendMoneyNetwork
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SendMoney2FaDialog: DialogFragment() {
    companion object {
        fun create(onItemAddedListener: () -> Unit): SendMoney2FaDialog {
            return SendMoney2FaDialog().apply {
                this.onItemAddedListener = onItemAddedListener
            }
        }
    }
    private var onItemAddedListener: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentSendMoney2faDialogBinding.inflate(inflater)
        //


        return MaterialAlertDialogBuilder(
            requireContext(), R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(binding.root)
            .setPositiveButton("Send") { _, _ ->
                runBlocking {
                    val job:Job = launch {
                    Repository.token2fa = binding.outlinedTextField2FaToken.editText?.text.toString()
                    SendMoney2FANetwork.sendMoney {
                        Repository.sendMoneyDataObj = SendMoney.Data(
                            amount = it.amount,
                            createdAt = it.createdAt,
                            description = it.description,
                            details = it.details,
                            id= it.id,
                            nativeAmount = it.nativeAmount,
                            network= it.network,
                            resource= it.resource,
                            resourcePath= it.resourcePath,
                            status= it.status,
                            to= it.to,
                            type=it.type,
                            updatedAt= it.updatedAt
                        )
                    }
                        onItemAddedListener()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}