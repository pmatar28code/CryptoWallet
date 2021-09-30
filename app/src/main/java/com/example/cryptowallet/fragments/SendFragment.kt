package com.example.cryptowallet.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.MainActivity
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
import com.example.cryptowallet.utilities.EncSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SendFragment: Fragment(R.layout.fragment_send) {
    @Inject lateinit var listAccountsNetwork: ListAccountsNetwork
    @Inject lateinit var addressNetwork: AddressNetwork
    @Inject lateinit var sendMoneyNetwork: SendMoneyNetwork
    private var walletSendAdapter:WalletSendAdapter?=null
    private lateinit var listOfWallets: MutableList<ListAccounts.Data>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSendBinding.bind(view)

        binding.scanQrCodeButton.setOnClickListener {
            val intent = Intent(requireContext(), ScanQrActivity::class.java)
            startActivity(intent)
        }

        listAccountsNetwork.getAccounts { list ->
            storeMostRecentTokenInEncSharedPreferences()
            listOfWallets = mutableListOf()
            for(wallet in list){
                if(wallet.balance?.amount != "0.00000000" && wallet.balance?.amount != "0.000000"
                    && wallet.balance?.amount != "0.0000" && wallet.balance?.amount != "0.0000000000"
                    && wallet.balance?.amount != "0.000000000" && wallet.balance?.amount != "0.0000000"){
                    listOfWallets.add(wallet)
                }
            }
            walletSendAdapter = WalletSendAdapter(resources) { data ->
                sendMoneyNetworkCallBackTasks(binding,data,resources)
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
            }
            performSearch(binding,listOfWallets)
        }
    }

    private fun performSearch(binding:FragmentSendBinding,listSearch:List<ListAccounts.Data>) {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query, listSearch)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText, listSearch)
                return true
            }
        })
    }

    private fun search(
        text: String?, listOfAccounts: List<ListAccounts.Data>
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

    private fun updateRecyclerView(searchResultList:List<ListAccounts.Data>) {
        walletSendAdapter?.submitList(searchResultList)
    }
    private fun sendMoneyButtonFunction(
        binding: FragmentSendBinding,
        requireContext: Context,
        parentFragmentManager: FragmentManager
    ) {
        Repository.sendMoneyAmount = binding.outlinedTextFieldAmount.editText?.text.toString()
        Repository.sendMonetTo = binding.outlinedTextFieldTo.editText?.text.toString()
        Repository.sendMoneyCurrency = binding.outlinedTextFieldCurrency.editText?.text.toString()

        sendMoneyNetwork.sendMoney {
            storeMostRecentTokenInEncSharedPreferences()
            if (Repository.repoSendMoneyResponseCode == 402) {
                sendMoneyNetworkIf(requireContext)
            } else {
                sendMoneyNetworkElse(requireContext,it)
            }
        }
    }
    private fun sendMoneyNetworkCallBackTasks(
        binding: FragmentSendBinding,
        data:ListAccounts.Data,
        resources:Resources
    ) {
        Repository.sendMoneyAccountId = data.id.toString()
        Repository.sendMoneyCurrency = data.balance?.currency.toString()
        binding.outlinedTextFieldCurrency.editText?.setText(Repository.sendMoneyCurrency)
        Repository.iconAddress = String.format(
            resources.getString(
                R.string.icon_address_send_fragment
            ),data.balance?.currency
        )

        addressNetwork.getAddresses {
            storeMostRecentTokenInEncSharedPreferences()
            Repository.address = it.address.toString()
        }
    }
    private fun storeMostRecentTokenInEncSharedPreferences(){
        val stringAccessToken = Repository.tempAccessToken?.let { accessToken ->
            EncSharedPreferences.convertTestClassToJsonString(
                accessToken
            )
        }
        if (stringAccessToken != null) {
            EncSharedPreferences.saveToEncryptedSharedPrefsString(
                MainActivity.keyStringAccesskey,
                stringAccessToken,
                requireContext()
            )
        }
    }

    private fun sendMoneyNetworkIf(requireContext: Context){
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
    }
    private fun sendMoneyNetworkElse(requireContext: Context,it:SendMoney.Data){
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







