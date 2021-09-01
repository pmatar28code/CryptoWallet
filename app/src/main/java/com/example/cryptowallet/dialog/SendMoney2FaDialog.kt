package com.example.cryptowallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentSendMoney2faDialogBinding
import com.example.cryptowallet.twilio.BasicAuthInterceptor
import com.example.cryptowallet.twilio.TwilioApi
import com.example.cryptowallet.twilio.TwilioReadMessages
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SendMoney2FaDialog: DialogFragment() {
    companion object {
        fun create(onItemAddedListener: () -> Unit): SendMoney2FaDialog {
            return SendMoney2FaDialog().apply {
                this.onItemAddedListener = onItemAddedListener
            }
        }
    }
    var authToken =""
    private var onItemAddedListener: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentSendMoney2faDialogBinding.inflate(inflater)

        val accountSID = "ACc27112fb4b4e922d6c19495ae01fa60b"
        val db = Firebase.firestore
        db.collection("Twilio")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    authToken = ""
                    val tempAuthToken = document.data
                    for(char in tempAuthToken.toString()){
                        if(char != '{' && char !='}' && char !='='){
                            authToken+=char
                        }
                    }
                    authToken = authToken.removePrefix("token")
                    /*
                    Need to delay, coinbase send sms to my phone, then my phone sends sms to twilio phone and
                    get the sms token here. Im using twilio so that you don't have to provide your
                    phone number in coinbase for 2FA
                    */
                    runBlocking {
                        delay(4000)
                        automaticallyGet2FATokenFromTwilio(
                            accountSID, authToken
                        ) {
                            binding.outlinedTextField2FaToken.editText?.setText(it)
                            Repository.token2fa = binding.outlinedTextField2FaToken.editText?.text.toString()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TEST ON FAILURE", "Error getting documents.", exception)
            }

        return MaterialAlertDialogBuilder(
            requireContext(), R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(binding.root)
            .setPositiveButton(getString(R.string.dialog_send)) { _, _ ->
                setPositiveButton(binding)
                onItemAddedListener()
            }
            .setNegativeButton(getString(R.string.dialog_cancel), null)
            .create()
    }
    private fun setPositiveButton(binding: FragmentSendMoney2faDialogBinding){
        Repository.token2fa = binding.outlinedTextField2FaToken.editText?.text.toString()
    }

    private fun automaticallyGet2FATokenFromTwilio(
        accountSID:String,authToken:String,twoFaTokenTwilioCallback:(String) -> Unit
    ){
        val logger = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(accountSID,authToken))
            .addInterceptor(logger)
            .build()
        val retrofitBuilder = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.twilio.com/2010-04-01/Accounts/")
            .addConverterFactory(MoshiConverterFactory.create())
        val retrofit = retrofitBuilder.build()
        val twilioClient = retrofit.create(TwilioApi::class.java)
        val twilioCall = twilioClient.getMessages()
        twilioCall.enqueue(object: Callback<TwilioReadMessages> {
            override fun onResponse(
                call: Call<TwilioReadMessages>, response: Response<TwilioReadMessages>
            ) {
                val theActualMessageWithToken = response.body()?.messages?.get(1)?.body.toString()
                val actualCleanTokenFromMessage =
                    getTheCorrectTokenMessageAndCleanIt(theActualMessageWithToken)
                twoFaTokenTwilioCallback(actualCleanTokenFromMessage)
            }
            override fun onFailure(call: Call<TwilioReadMessages>, t: Throwable) {
                Log.e("ON FAILURE","$t")
                twoFaTokenTwilioCallback("Did not get the Token")
            }
        })
    }
    fun getTheCorrectTokenMessageAndCleanIt(theActualMessageWithToken:String):String{
        var theCorrectMessageWithToken =""
        for(char in theActualMessageWithToken){
            if(char != ' ' && char != '.' && char != ';' && char != 'Y'){
                theCorrectMessageWithToken+=char
            }
        }
        return theCorrectMessageWithToken
    }
}