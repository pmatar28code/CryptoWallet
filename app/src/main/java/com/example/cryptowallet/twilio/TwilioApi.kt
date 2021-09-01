package com.example.cryptowallet.twilio

import retrofit2.Call
import retrofit2.http.GET

interface TwilioApi {
    @GET("AC4698942c2ff41f84319940ff2868cbdf/Messages.json?PageSize=20")
    fun getMessages(): Call<TwilioReadMessages>
}