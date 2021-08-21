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
import com.example.cryptowallet.network.networkcalls.SendMoney2FANetwork
import com.example.cryptowallet.network.networkcalls.SendMoneyNetwork
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SendMoney2FaDialog: DialogFragment() {
    companion object {
        fun create(listener: () -> Unit): SendMoney2FaDialog {
            return SendMoney2FaDialog().apply {

            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentSendMoney2faDialogBinding.inflate(inflater)
        //


        return MaterialAlertDialogBuilder(
            requireContext(), R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(binding.root)
            .setPositiveButton("Send") { _, _ ->
                Repository.token2fa = binding.outlinedTextField2FaToken.editText?.text.toString()
                SendMoney2FANetwork.sendMoney {
                    Log.e("2Fa REQUEST MONEy FROM DiALOG PRESSING ACCEPT:","${it.details}")
                    Toast.makeText(requireContext(),"Details for send money 2Fa: ${it.createdAt}",Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}