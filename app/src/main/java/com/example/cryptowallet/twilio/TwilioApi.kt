package com.example.cryptowallet.twilio

import retrofit2.Call
import retrofit2.http.GET

interface TwilioApi {
    @GET("ACac97259d53757508e130b6c4b62eb7da/Messages.json?PageSize=20")
    fun getMessages(): Call<TwilioReadMessages>
}