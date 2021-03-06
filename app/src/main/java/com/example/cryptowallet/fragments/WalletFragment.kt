package com.example.cryptowallet.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.MainActivity
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.adapter.WalletsAdapter
import com.example.cryptowallet.databinding.FragmentWalletBinding
import com.example.cryptowallet.dialog.WalletDetailsDialog
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.example.cryptowallet.utilities.EncSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WalletFragment: Fragment(R.layout.fragment_wallet) {
    companion object{
        var listOfAccountsWithCondition = mutableListOf<ListAccounts.Data>()
        var walletAdapter:WalletsAdapter ?= null
    }
    @Inject
    lateinit var listAccountsNetwork: ListAccountsNetwork

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWalletBinding.bind(view)

        justWalletAccountsWithCurrencyBTCAndWLUNA()

        walletAdapter = WalletsAdapter(resources) {
            Repository.walletDetailsAccountId = it.id.toString()
            WalletDetailsDialog.create {
            }.show(parentFragmentManager,"to open wallet details dialog")
        }
        binding.walletsRecyclerView.apply {
            adapter = walletAdapter
            layoutManager = LinearLayoutManager(requireContext())
            walletAdapter!!.submitList(Repository.accounts.reversed())
        }
    }
    private fun justWalletAccountsWithCurrencyBTCAndWLUNA(){
        for(account in Repository.accounts){
            if(account.balance?.currency == "BTC" || account.balance?.currency == "WLUNA"){
                listOfAccountsWithCondition.add(account)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        listAccountsNetwork.getAccounts {
            storeMostRecentTokenInEncSharedPreferences()
            Repository.accounts = it as MutableList<ListAccounts.Data>
            walletAdapter?.submitList(Repository.accounts.reversed())
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
}