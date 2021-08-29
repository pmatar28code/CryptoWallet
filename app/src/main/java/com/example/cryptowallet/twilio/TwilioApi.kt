package com.example.cryptowallet.twilio

import retrofit2.Call
import retrofit2.http.GET

interface TwilioApi {
    @GET("AC41847eef5c541ba476941edcbfd2e51a/Messages.json?PageSize=20")
    fun getMessages(): Call<TwilioReadMessages>
}