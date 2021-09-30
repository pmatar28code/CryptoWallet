package com.example.cryptowallet.fragments

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.MainActivity
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.adapter.WalletRequestAdapter
import com.example.cryptowallet.databinding.FragmentRequestBinding
import com.example.cryptowallet.dialog.RequestMoneyDialog
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.AddressNetwork
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.utilities.EncSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RequestFragment: Fragment(R.layout.fragment_request) {
    @Inject lateinit var listAccountsNetwork: ListAccountsNetwork
    @Inject lateinit var addressNetwork: AddressNetwork
    var walletsRequestAdapter:WalletRequestAdapter ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRequestBinding.bind(view)
        var listOfWallets = mutableListOf<ListAccounts.Data>()
        listAccountsNetwork.getAccounts { list ->
            storeMostRecentTokenInEncSharedPreferences()
            listOfWallets = list.toMutableList()
            walletsRequestAdapter = WalletRequestAdapter { data ->
                Repository.accountId = data.id.toString()
                Repository.currency = data.balance?.currency.toString()
                Repository.iconAddress = String.format(
                    resources.getString(
                        R.string.icon_address_request_fragment
                    ),data.balance?.currency
                )

                addressNetwork.getAddresses {
                    Repository.address = it.address.toString()
                    RequestMoneyDialog.create {
                    }.show(parentFragmentManager, "Open Edit Recipe")
                }
            }
            binding.walletsRequestRecyclerView.apply{
                adapter = walletsRequestAdapter
                layoutManager = LinearLayoutManager(context)
                walletsRequestAdapter?.submitList(listOfWallets.toList().reversed())
            }
            performSearch(binding,listOfWallets)
        }
    }
    private fun performSearch(
        binding: FragmentRequestBinding, listSearch:List<ListAccounts.Data>
    ) {
        binding.searchViewRequest.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
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
        text: String?,listOfAccounts:List<ListAccounts.Data>,binding: FragmentRequestBinding
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
        walletsRequestAdapter?.submitList(searchResultList)
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