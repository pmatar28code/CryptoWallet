package com.example.cryptowallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptowallet.adapter.WalletRequestAdapter
import com.example.cryptowallet.databinding.ActivityShowAddressesForDifferentWalletsBinding
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.example.cryptowallet.network.networkcalls.AddressNetwork
import com.example.cryptowallet.network.networkcalls.ListAccountsNetwork
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ShowAddressesForDifferentWalletsActivity: AppCompatActivity() {
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        val inflater = LayoutInflater.from(this)
        val binding = ActivityShowAddressesForDifferentWalletsBinding.inflate(inflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        var listOfWallets = mutableListOf<ListAccounts.Data>()

        runBlocking {
            var job: Job = launch {
                ListAccountsNetwork.getAccounts { list ->
                    Log.e("INSIDE WALLET ACTIVITY NETWORK CALL:", "$list")
                    listOfWallets = list.toMutableList()

                    Log.e("Wallets Activity list:","$listOfWallets")

                    val walletsAdapter =WalletRequestAdapter(onCLickSetId = { data ->
                        Repository.accountId = data.id.toString()
                        Repository.currency = data.balance?.currency.toString()
                        AddressNetwork.getAddresses {
                            //var accountAddress = "1232342342344-1231313-123123"
                            var urlForQr =
                                "http://api.qrserver.com/v1/create-qr-code/?data=${it.address}&size=100x100"
                            Picasso.get().load(urlForQr).into(binding.imageViewAddressesQr)
                        }
                    })

                    binding.walletsRecyclerView2.apply{
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