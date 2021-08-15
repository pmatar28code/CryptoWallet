package com.example.cryptowallet.network.classesapi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RequestMoneyApiBody(
    @Json(name = "type")
    val type:String,
    @Json(name = "to")
    val to:String,
    @Json(name = "amount")
    val amount:String,
    @Json(name = "currency")
    val currency:String,
    @Json(name = "description")
    val description:String
    )
