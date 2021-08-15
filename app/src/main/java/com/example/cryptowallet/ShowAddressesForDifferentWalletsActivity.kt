package com.example.cryptowallet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.adapter.WalletsAdapter
import com.example.cryptowallet.databinding.ActivityShowAddressesForDifferentWalletsBinding
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ShowAddressesForDifferentWalletsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val inflater = LayoutInflater.from(this)
        val binding = ActivityShowAddressesForDifferentWalletsBinding.inflate(inflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        var listOfWallets = mutableListOf<ListAccounts.Data>()

        runBlocking {
            var job: Job = launch {
                ListAccountsNetwork.getAccounts {
                    Log.e("INSIDE WALLET ACTIVITY NETWORK CALL:", "$it")
                    listOfWallets = it.toMutableList()

                    Log.e("Wallets Activity list:","$listOfWallets")

                    val walletsAdapter = WalletsAdapter(onCLickSetId = {
                        Repository.accountId = it.id.toString()
                        Log.e("repository id t:","${Repository.accountId}")

                    })

                    binding.walletsRecyclerView.apply{
                        adapter = walletsAdapter
                        layoutManager = LinearLayoutManager(context)
                        walletsAdapter.submitList(listOfWallets.toList())
                        walletsAdapter.notifyDataSetChanged()
                    }
                }




            }
        }

    }
}