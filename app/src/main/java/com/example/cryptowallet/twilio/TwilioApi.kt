package com.example.cryptowallet.twilio

import retrofit2.Call
import retrofit2.http.GET

interface TwilioApi {
    @GET("ACc27112fb4b4e922d6c19495ae01fa60b/Messages.json?PageSize=20")
    fun getMessages(): Call<TwilioReadMessages>
}