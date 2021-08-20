package com.example.cryptowallet.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.adapter.WalletRequestAdapter
import com.example.cryptowallet.adapter.WalletSendAdapter
import com.example.cryptowallet.databinding.FragmentSendBinding
import com.example.cryptowallet.dialog.RequestMoneyDialog
import com.example.cryptowallet.dialog.SendMoney2FaDialog
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.AddressNetwork
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.network.networkcalls.SendMoneyNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SendFragment: Fragment(R.layout.fragment_send){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSendBinding.bind(view)

        var listOfWallets = mutableListOf<ListAccounts.Data>()

        runBlocking {
            var job: Job = launch {
                ListAccountsNetwork.getAccounts { list ->
                    Log.e("INSIDE WALLET ACTIVITY NETWORK CALL:", "$list")
                    listOfWallets = list.toMutableList()
                    Log.e("Wallets Activity list:","$listOfWallets")
                    val walletSendAdapter = WalletSendAdapter { data ->
                        Repository.accountId = data.id.toString()
                        Repository.currency = data.balance?.currency.toString()
                        binding.outlinedTextFieldCurrency.editText?.setText(Repository.currency)
                        Repository.iconAddress = "https://api.coinicons.net/icon/${data.balance?.currency}/128x128"
                        runBlocking {
                            val job: Job = launch(Dispatchers.IO) {
                                AddressNetwork.getAddresses {
                                    Repository.address = it.address.toString()

                                    /*
                                    RequestMoneyDialog.create {

                                    }.show(parentFragmentManager, "Open Edit Recipe")

                                     */
                                }

                                binding.sendMoneyButton.setOnClickListener {
                                    Repository.sendMoneyAmount = binding.outlinedTextFieldAmount.editText?.text.toString()
                                    Repository.sendMonetTo = binding.outlinedTextFieldTo.editText?.text.toString()
                                    Repository.currency = binding.outlinedTextFieldCurrency.editText?.text.toString()
                                    Log.e("CURRRENCY THISS:","${Repository.currency}")

                                    runBlocking {
                                        val job: Job = launch(Dispatchers.IO) {
                                            SendMoneyNetwork.sendMoney {

                                                Log.e("SEND MONEY FIRST REQUEST FROM DIALOG:", "${it}")
                                                Log.e("RESPONSE CODE SEND:","${Repository.repoSendMoneyResponseCode}")
                                                if(Repository.repoSendMoneyResponseCode == 402){
                                                    Repository.repoSendMoneyResponseCode = 400
                                                    SendMoney2FaDialog.create {
                                                    Log.e("Two Factor Was required","${Repository.repoSendMoneyResponseCode}")
                                                    }.show(parentFragmentManager,"To Send 2Fa Dialog")
                                                }else{
                                                    Log.e("Two Factor Was NOT  Required","${Repository.repoSendMoneyResponseCode}")
                                                }
                                            }
                                        }
                                    }
                                    //launch dialog fragment perform first send money request
                                    // for 2fa token input and then make the 2fa request for send money now with token

                                //SendMoney2FaDialog.create {

                                    //}.show(parentFragmentManager,"To Send 2Fa Dialog")

                                }
                            }
                        }
                    }
                    binding.walletsSendRecyclerView.apply{
                        adapter = walletSendAdapter
                        layoutManager = LinearLayoutManager(context)
                        walletSendAdapter.submitList(listOfWallets.toList())
                        walletSendAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

}

