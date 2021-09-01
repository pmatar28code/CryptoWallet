package com.example.cryptowallet.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.ScanQrActivity
import com.example.cryptowallet.adapter.WalletSendAdapter
import com.example.cryptowallet.databinding.FragmentSendBinding
import com.example.cryptowallet.dialog.SendMoney2FaDialog
import com.example.cryptowallet.dialog.SendMoneyConfirmDialog
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.classesapi.SendMoney
import com.example.cryptowallet.network.networkcalls.AddressNetwork
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.network.networkcalls.SendMoneyNetwork
import java.util.*

class SendFragment: Fragment(R.layout.fragment_send) {
    private var walletSendAdapter:WalletSendAdapter?=null
    private lateinit var listOfWallets: MutableList<ListAccounts.Data>
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSendBinding.bind(view)

        binding.scanQrCodeButton.setOnClickListener {
            val intent = Intent(requireContext(), ScanQrActivity::class.java)
            startActivity(intent)
        }

        ListAccountsNetwork.getAccounts { list ->
            listOfWallets = mutableListOf()
            for(wallet in list){
                if(wallet.balance?.amount != "0.00000000" && wallet.balance?.amount != "0.000000"
                    && wallet.balance?.amount != "0.0000" && wallet.balance?.amount != "0.0000000000"
                    && wallet.balance?.amount != "0.000000000" && wallet.balance?.amount != "0.0000000"){
                    listOfWallets.add(wallet)
                }
            }
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
                walletSendAdapter?.submitList(listOfWallets.toList().reversed())
                walletSendAdapter?.notifyDataSetChanged()
            }
            performSearch(binding,listOfWallets)
        }
    }

    private fun performSearch(binding:FragmentSendBinding,listSearch:List<ListAccounts.Data>) {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query,listSearch,binding)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText,listSearch,binding)
                return true
            }
        })
    }

    private fun search(
        text: String?,listOfAccounts:List<ListAccounts.Data>,binding:FragmentSendBinding
    ) {
        val listOfAccountsToWork = listOfAccounts
        val searchResultList = mutableListOf<ListAccounts.Data>()
        text?.let {
            listOfAccountsToWork.forEach { Account ->
                if (Account.balance?.currency == text.uppercase(Locale.getDefault()) ||
                    Account.balance?.currency?.contains(text.uppercase(Locale.getDefault())) == true
                ) {
                    searchResultList.add(Account)
                }
            }
            if(searchResultList.isEmpty()){
                updateRecyclerView(listOfAccountsToWork.reversed())
            }else{
                updateRecyclerView(searchResultList.reversed())
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(searchResultList:List<ListAccounts.Data>) {
        walletSendAdapter?.submitList(searchResultList)
        walletSendAdapter?.notifyDataSetChanged()
    }
    private fun sendMoneyButtonFunction(
        binding: FragmentSendBinding,
        requireContext: Context,
        parentFragmentManager: FragmentManager
    ) {
        Repository.sendMoneyAmount = binding.outlinedTextFieldAmount.editText?.text.toString()
        Repository.sendMonetTo = binding.outlinedTextFieldTo.editText?.text.toString()
        Repository.sendMoneyCurrency = binding.outlinedTextFieldCurrency.editText?.text.toString()

        SendMoneyNetwork.sendMoney {
            if (Repository.repoSendMoneyResponseCode == 402) {
                Repository.repoSendMoneyResponseCode = 400
                Toast.makeText(
                    requireContext,
                    "Two Factor Was Required",
                    Toast.LENGTH_SHORT
                ).show()
                Repository.didntRequiredTwoFA = false
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

                Toast.makeText(
                    requireContext,
                    "TWO FACTOR ID NOT REQUIRED ${it.createdAt}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun sendMoneyNetworkCallBackTasks(
        binding: FragmentSendBinding,data:ListAccounts.Data
    ) {
        Repository.sendMoneyAccountId = data.id.toString()
        Repository.sendMoneyCurrency = data.balance?.currency.toString()
        binding.outlinedTextFieldCurrency.editText?.setText(Repository.sendMoneyCurrency)
        Repository.iconAddress = "https://api.coinicons.net/icon/${data.balance?.currency}/128x128"

        AddressNetwork.getAddresses {
            Repository.address = it.address.toString()
        }
    }
}







