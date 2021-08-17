package com.example.cryptowallet.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.ShowAddressesForDifferentWalletsActivity
import com.example.cryptowallet.databinding.FragmentWalletBinding
import com.example.cryptowallet.network.classesapi.ListAccounts

class WalletFragment: Fragment(R.layout.fragment_wallet) {
    companion object{
        var listOfAccountsWithCondition = mutableListOf<ListAccounts.Data>()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWalletBinding.bind(view)

        justWalletAccountsWithCurrencyBTCAndWLUNA()

        binding.apply {
            accountsListText.text = listOfAccountsWithCondition.toString()
            accountNameText.text = Repository.userName
            buttontoAccounts.setOnClickListener {
                val intent = Intent(requireContext(),ShowAddressesForDifferentWalletsActivity::class.java)
                startActivity(intent)
            }
        }
    }


    fun justWalletAccountsWithCurrencyBTCAndWLUNA(){
        for(account in Repository.accounts){
            if(account.balance?.currency == "BTC" || account.balance?.currency == "WLUNA"){
                listOfAccountsWithCondition.add(account)
            }
        }
    }
}