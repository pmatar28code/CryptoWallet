package com.example.cryptowallet.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.adapter.WalletSendAdapter
import com.example.cryptowallet.databinding.FragmentSendBinding
import com.example.cryptowallet.dialog.SendMoney2FaDialog
import com.example.cryptowallet.dialog.SendMoneyConfirmDialog
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.classesapi.SendMoney
import com.example.cryptowallet.network.networkcalls.AddressNetwork
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.network.networkcalls.SendMoneyNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SendFragment: Fragment(R.layout.fragment_send) {
    private var walletSendAdapter:WalletSendAdapter?=null
    private lateinit var listOfWallets: MutableList<ListAccounts.Data>
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSendBinding.bind(view)

        runBlocking {
            var job: Job = launch {
                ListAccountsNetwork.getAccounts { list ->
                    listOfWallets = list.toMutableList()
                    walletSendAdapter = WalletSendAdapter { data ->
                        sendMoneyNetworkCallBackTasks(binding,data)

                        binding.sendMoneyButton.setOnClickListener {
                            sendMoneyButtonFunction(
                                binding,
                                requireContext(),
                                parentFragmentManager
                            )
                        }
                    }

                    binding.walletsSendRecyclerView.apply {
                        adapter = walletSendAdapter
                        layoutManager = LinearLayoutManager(context)
                        walletSendAdapter?.submitList(listOfWallets.toList())
                        walletSendAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun sendMoneyButtonFunction(
        binding: FragmentSendBinding,
        requireContext: Context,
        parentFragmentManager: FragmentManager
    ) {
        Repository.sendMoneyAmount = binding.outlinedTextFieldAmount.editText?.text.toString()
        Repository.sendMonetTo = binding.outlinedTextFieldTo.editText?.text.toString()
        Repository.sendMoneyCurrency = binding.outlinedTextFieldCurrency.editText?.text.toString()
        Log.e("CURRRENCY THISS:", Repository.currency)

        runBlocking {
            val job: Job = launch(Dispatchers.IO) {
                SendMoneyNetwork.sendMoney {
                    Log.e("SEND MONEY FIRST REQUEST FROM DIALOG:", "$it")
                    Log.e("RESPONSE CODE SEND:", "${Repository.repoSendMoneyResponseCode}")
                    if (Repository.repoSendMoneyResponseCode == 402) {
                        Repository.repoSendMoneyResponseCode = 400
                        Toast.makeText(
                            requireContext,
                            "Two Factor Was Required",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("Two Factor Was required", "${Repository.repoSendMoneyResponseCode}")
                        SendMoney2FaDialog.create {
                            SendMoneyConfirmDialog.create {

                            }.show(parentFragmentManager, "From Send Money to Send Confirm")
                        }.show(parentFragmentManager, "To Send 2Fa Dialog")
                    } else {
                        Repository.didntRequiredTwoFA = true
                        Repository.sendMoneyDataObj = SendMoney.Data(
                            amount = it.amount,
                            createdAt = it.createdAt,
                            description = it.description,
                            details = it.details,
                            id = it.id,
                            nativeAmount = it.nativeAmount,
                            network = it.network,
                            resource = it.resource,
                            resourcePath = it.resourcePath,
                            status = it.status,
                            to = it.to,
                            type = it.type,
                            updatedAt = it.updatedAt
                        )
                        SendMoneyConfirmDialog.create {


                        }.show(parentFragmentManager, "From Send Money to Send Confirm")

                        Log.e(
                            "Two Factor Was NOT  Required",
                            "${Repository.repoSendMoneyResponseCode}"
                        )
                        Toast.makeText(
                            requireContext,
                            "TWO FACTOR NOT REQUIRED ${it.createdAt}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    //launch dialog fragment perform first send money request
    // for 2fa token input and then make the 2fa request for send money now with token

    //SendMoney2FaDialog.create {

    //}.show(parentFragmentManager,"To Send 2Fa Dialog")

    private fun sendMoneyNetworkCallBackTasks(binding: FragmentSendBinding,data:ListAccounts.Data) {
        //Log.e("INSIDE WALLET ACTIVITY NETWORK CALL:", "$list")
        //listOfWallets = list.toMutableList()
        //Log.e("Wallets Activity list:", "$listOfWallets")

        Repository.sendMoneyAccountId = data.id.toString()
        Repository.sendMoneyCurrency = data.balance?.currency.toString()
        binding.outlinedTextFieldCurrency.editText?.setText(Repository.sendMoneyCurrency)
        Repository.iconAddress =
            "https://api.coinicons.net/icon/${data.balance?.currency}/128x128"
        runBlocking {
            val job: Job = launch(Dispatchers.IO) {
                AddressNetwork.getAddresses {
                    Repository.address = it.address.toString()
                /*
                    RequestMoneyDialog.create {
                }.show(parentFragmentManager, "Open Edit Recipe")

                */
                }
            }
        }
    }
}







