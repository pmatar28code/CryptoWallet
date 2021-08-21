package com.example.cryptowallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentSendMoney2faDialogBinding
import com.example.cryptowallet.network.apis.SendMoney2FAAPI
import com.example.cryptowallet.network.classesapi.SendMoney
import com.example.cryptowallet.oauth.AccessTokenProviderImp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SendMoney2FaDialog: DialogFragment() {
    companion object {
        fun create(onItemAddedListener: () -> Unit): SendMoney2FaDialog {
            return SendMoney2FaDialog().apply {
                this.onItemAddedListener = onItemAddedListener
            }
        }
    }
    private var onItemAddedListener: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentSendMoney2faDialogBinding.inflate(inflater)

        return MaterialAlertDialogBuilder(
            requireContext(), R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(binding.root)
            .setPositiveButton("Send") { _, _ ->
                setPositiveButton(binding)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
    private fun setPositiveButton(binding: FragmentSendMoney2faDialogBinding){
        runBlocking {
            val job:Job = launch {
                Repository.token2fa = binding.outlinedTextField2FaToken.editText?.text.toString()
                /*
                val token = AccessTokenProviderImp().token()?.access_token?:""
                var token2fa = Repository.token2fa
                val accountId = Repository.sendMoneyAccountId
                val to = Repository.sendMonetTo
                val currency = Repository.sendMoneyCurrency
                val amount = Repository.sendMoneyAmount

                val retrofitBuilder = Retrofit.Builder()
                    .baseUrl("https://api.coinbase.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                val retrofit = retrofitBuilder.build()
                val sendMoney2FaClient = retrofit.create(SendMoney2FAAPI::class.java)
                val sendMoney2FaCall = sendMoney2FaClient.sendMoney(
                  "Bearer $token",token2fa,accountId,"send",to,amount,currency
                )
                sendMoney2FaCall.enqueue(object: Callback<SendMoney.Data>{
                    override fun onResponse(
                        call: Call<SendMoney.Data>,
                        response: Response<SendMoney.Data>
                    ) {
                        if(response.body() != null) {
                            Repository.sendMoneyDataObj = response.body()!!
                        }
                    }

                    override fun onFailure(call: Call<SendMoney.Data>, t: Throwable) {
                        Log.e("SEND2FADIALOG NETWORK ON FAILURE","$t")
                    }
                })*/
                onItemAddedListener()
                /*
                SendMoney2FANetwork.sendMoney {
                    Repository.sendMoneyDataObj = SendMoney.Data(
                        amount = it.amount,
                        createdAt = it.createdAt,
                        description = it.description,
                        details = it.details,
                        id= it.id,
                        nativeAmount = it.nativeAmount,
                        network= it.network,
                        resource= it.resource,
                        resourcePath= it.resourcePath,
                        status= it.status,
                        to= it.to,
                        type=it.type,
                        updatedAt= it.updatedAt
                    )
                }*/

            }
        }
    }
}