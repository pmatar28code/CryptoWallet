package com.example.cryptowallet.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.adapter.WalletRequestAdapter
import com.example.cryptowallet.databinding.FragmentShowTransactionsBinding
import com.example.cryptowallet.dialog.TransactionsDetailDialog
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import java.util.*

class ShowTransactionsFragment: Fragment(R.layout.fragment_show_transactions) {
    @SuppressLint("NotifyDataSetChanged")
    var walletsShowTransactionsAdapter:WalletRequestAdapter ?= null
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentShowTransactionsBinding.bind(view)

        val listOfWallets = mutableListOf<ListAccounts.Data>()

        setVariablesAndRecyclerView(binding,listOfWallets)
    }

    private fun performSearch(
        binding: FragmentShowTransactionsBinding, listSearch:List<ListAccounts.Data>
    ) {
        binding.searchViewShowTransactions.setOnQueryTextListener(
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
        text: String?,listOfAccounts:List<ListAccounts.Data>,
        binding: FragmentShowTransactionsBinding
    ){
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
        walletsShowTransactionsAdapter?.submitList(searchResultList)
        walletsShowTransactionsAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setVariablesAndRecyclerView(
        binding: FragmentShowTransactionsBinding, listOfWallets: MutableList<ListAccounts.Data>
    ){
        ListAccountsNetwork.getAccounts { list ->
            for(account in list){
                if(account.balance?.amount != "0.00000000" && account.balance?.amount != "0.000000"
                    && account.balance?.amount != "0.0000" && account.balance?.amount != "0.0000000000"
                    && account.balance?.amount != "0.000000000" && account.balance?.amount != "0.0000000"){
                    listOfWallets.add(account)
                }

            }
            walletsShowTransactionsAdapter = WalletRequestAdapter { data ->
                Repository.setTransactionIdForSpecificNetworkRequest = data.id.toString()
                Repository.setTransactionCurrencyForIcon = data.balance?.currency.toString()
                Repository.iconAddress = "https://api.coinicons.net/icon/${data.balance?.currency}/128x128"
                TransactionsDetailDialog.create {

                }.show(parentFragmentManager,"open wallet transaction details")

            }
            binding.walletsShowTransactionsRecyclerView.apply{
                adapter = walletsShowTransactionsAdapter
                layoutManager = LinearLayoutManager(context)
                walletsShowTransactionsAdapter?.submitList(listOfWallets.toList().reversed())
                walletsShowTransactionsAdapter?.notifyDataSetChanged()
            }
            performSearch(binding,listOfWallets)
        }
    }
}