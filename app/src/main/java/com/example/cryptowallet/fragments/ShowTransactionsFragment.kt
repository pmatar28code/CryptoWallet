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
import com.example.cryptowallet.databinding.FragmentShowTransactionsBinding
import com.example.cryptowallet.dialog.TransactionsDetailDialog
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.utilities.EncSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ShowTransactionsFragment: Fragment(R.layout.fragment_show_transactions) {
    private var walletsShowTransactionsAdapter:WalletRequestAdapter ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentShowTransactionsBinding.bind(view)

        val listOfWallets = mutableListOf<ListAccounts.Data>()

        setVariablesAndRecyclerView(binding,listOfWallets)
    }
    @Inject
    lateinit var listAccountsNetwork: ListAccountsNetwork

    private fun performSearch(
        binding: FragmentShowTransactionsBinding, listSearch:List<ListAccounts.Data>
    ) {
        binding.searchViewShowTransactions.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
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

    private fun updateRecyclerView(searchResultList:List<ListAccounts.Data>) {
        walletsShowTransactionsAdapter?.submitList(searchResultList)
    }

    private fun setVariablesAndRecyclerView(
        binding: FragmentShowTransactionsBinding, listOfWallets: MutableList<ListAccounts.Data>
    ){
        listAccountsNetwork.getAccounts { list ->
            storeMostRecentTokenInEncSharedPreferences()
            for(account in list){
                if(account.balance?.amount != "0.00000000" && account.balance?.amount != "0.000000"
                    && account.balance?.amount != "0.0000" && account.balance?.amount != "0.0000000000"
                    && account.balance?.amount != "0.000000000" && account.balance?.amount != "0.0000000"){
                    listOfWallets.add(account)
                }

            }
            walletsShowTransactionsAdapter = WalletRequestAdapter(resources) { data ->
                Repository.setTransactionIdForSpecificNetworkRequest = data.id.toString()
                Repository.setTransactionCurrencyForIcon = data.balance?.currency.toString()
                Repository.iconAddress = String.format(
                    resources.getString(
                        R.string.icon_address_show_transactions_fragment
                    ),
                    data.balance?.currency?.lowercase(Locale.getDefault()
                    )
                )

                TransactionsDetailDialog.create {

                }.show(parentFragmentManager,"open wallet transaction details")

            }
            binding.walletsShowTransactionsRecyclerView.apply{
                adapter = walletsShowTransactionsAdapter
                layoutManager = LinearLayoutManager(context)
                walletsShowTransactionsAdapter?.submitList(listOfWallets.toList().reversed())
            }
            performSearch(binding,listOfWallets)
        }
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