package com.example.cryptowallet.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.adapter.WalletRequestAdapter
import com.example.cryptowallet.databinding.FragmentRequestBinding
import com.example.cryptowallet.dialog.RequestMoneyDialog
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.AddressNetwork
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RequestFragment: Fragment(R.layout.fragment_request) {
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRequestBinding.bind(view)

        var listOfWallets = mutableListOf<ListAccounts.Data>()

        runBlocking {
            var job: Job = launch {
                ListAccountsNetwork.getAccounts { list ->
                    Log.e("INSIDE WALLET ACTIVITY NETWORK CALL:", "$list")
                    listOfWallets = list.toMutableList()
                    Log.e("Wallets Activity list:","$listOfWallets")
                    val walletsRequestAdapter = WalletRequestAdapter { data ->
                        Repository.accountId = data.id.toString()
                        Repository.currency = data.balance?.currency.toString()
                        Repository.iconAddress = "https://api.coinicons.net/icon/${data.balance?.currency}/128x128"
                        runBlocking {
                            val job: Job = launch(IO) {
                                AddressNetwork.getAddresses {
                                    Repository.address = it.address.toString()
                                    RequestMoneyDialog.create {

                                    }.show(parentFragmentManager, "Open Edit Recipe")
                                }
                            }
                        }
                    }
                    binding.walletsRequestRecyclerView.apply{
                        adapter = walletsRequestAdapter
                        layoutManager = LinearLayoutManager(context)
                        walletsRequestAdapter.submitList(listOfWallets.toList())
                        walletsRequestAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}