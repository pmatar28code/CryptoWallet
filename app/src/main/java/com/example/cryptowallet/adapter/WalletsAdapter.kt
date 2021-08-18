package com.example.cryptowallet.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptowallet.R
import com.example.cryptowallet.databinding.ItemWalletsBinding
import com.example.cryptowallet.network.apis.CryptoIconApi
import com.example.cryptowallet.network.classesapi.ListAccounts
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WalletsAdapter(
    val onCLickSetId:(ListAccounts.Data) -> Unit
): ListAdapter<ListAccounts.Data, WalletsAdapter.WalletsViewHolder>(diff) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<ListAccounts.Data>() {
            override fun areItemsTheSame(
                oldItem: ListAccounts.Data,
                newItem: ListAccounts.Data
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListAccounts.Data,
                newItem: ListAccounts.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWalletsBinding.inflate(inflater, parent, false)
        return WalletsViewHolder(binding, onCLickSetId)
    }

    override fun onBindViewHolder(holder: WalletsViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener { onCLickSetId(getItem(position)) }
        val currentWalletCurrency = getItem(position).balance?.currency
        if (currentWalletCurrency != null) {
            setIcon(currentWalletCurrency,holder)
        }
    }

    class WalletsViewHolder(
        private val binding: ItemWalletsBinding,
        private val onCLickForDetails: (ListAccounts.Data) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(wallet: ListAccounts.Data) {
            binding.apply {
                walletNameText.text = wallet.name
                walletIdText.text = wallet.id
                walletCurrencyText.text = wallet.balance?.currency
            }
        }
    }

    private fun setIcon(currency: String, holder:WalletsViewHolder){
        getIcons(currency) {
            if (it.contains(currency)) {
                Log.e("IT IN PICASSO:", it)
                Picasso.get().load(it).into(
                    holder.itemView
                        .findViewById<ImageView>(R.id.wallet_icon_image_view)
                )
            }
        }
    }

    private fun getIcons(currency:String, listCallback:(String) -> Unit) {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://api.coinicons.net/")
            .addConverterFactory(GsonConverterFactory.create())
        val retrofit = retrofitBuilder.build()
        val cryptoIconClient = retrofit.create(CryptoIconApi::class.java)
        val cryptoIconCall = cryptoIconClient.getIcons(currency)
        cryptoIconCall.enqueue(object:Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                var linkString = response.toString()
                var lenght = linkString.length
                linkString= linkString.substring(46)
                linkString = linkString.dropLast(1)// .substr(37, lenght)
                linkString = linkString.replace("%","")
                linkString = linkString.replace("7","")
                linkString = linkString.replace("D","")
                listCallback(linkString)
                Log.e("LINK STRING ADAPTER FUN:","$linkString")
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ON FAILURE ADAPTER","$t")
            }
        })
    }
}