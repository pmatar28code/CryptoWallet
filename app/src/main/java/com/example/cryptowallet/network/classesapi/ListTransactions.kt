package com.example.cryptowallet.network.classesapi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListTransactions(
    @Json(name = "data")
    val data: List<Data?>?,
    @Json(name = "pagination")
    val pagination: Pagination?
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "amount")
        val amount: Amount?,
        @Json(name = "buy")
        val buy: Buy?,
        @Json(name = "created_at")
        val createdAt: String?,
        @Json(name = "description")
        val description: Any?,
        @Json(name = "details")
        val details: Details?,
        @Json(name = "id")
        val id: String?,
        @Json(name = "native_amount")
        val nativeAmount: NativeAmount?,
        @Json(name = "network")
        val network: Network?,
        @Json(name = "resource")
        val resource: String?,
        @Json(name = "resource_path")
        val resourcePath: String?,
        @Json(name = "status")
        val status: String?,
        @Json(name = "to")
        val to: To?,
        @Json(name = "type")
        val type: String?,
        @Json(name = "updated_at")
        val updatedAt: String?
    ) {
        @JsonClass(generateAdapter = true)
        data class Amount(
            @Json(name = "amount")
            val amount: String?,
            @Json(name = "currency")
            val currency: String?
        )

        @JsonClass(generateAdapter = true)
        data class Buy(
            @Json(name = "id")
            val id: String?,
            @Json(name = "resource")
            val resource: String?,
            @Json(name = "resource_path")
            val resourcePath: String?
        )

        @JsonClass(generateAdapter = true)
        data class Details(
            @Json(name = "subtitle")
            val subtitle: String?,
            @Json(name = "title")
            val title: String?
        )

        @JsonClass(generateAdapter = true)
        data class NativeAmount(
            @Json(name = "amount")
            val amount: String?,
            @Json(name = "currency")
            val currency: String?
        )

        @JsonClass(generateAdapter = true)
        data class Network(
            @Json(name = "name")
            val name: String?,
            @Json(name = "status")
            val status: String?
        )

        @JsonClass(generateAdapter = true)
        data class To(
            @Json(name = "email")
            val email: String?,
            @Json(name = "id")
            val id: String?,
            @Json(name = "resource")
            val resource: String?,
            @Json(name = "resource_path")
            val resourcePath: String?
        )
    }

    @JsonClass(generateAdapter = true)
    data class Pagination(
        @Json(name = "ending_before")
        val endingBefore: Any?,
        @Json(name = "limit")
        val limit: Int?,
        @Json(name = "next_uri")
        val nextUri: Any?,
        @Json(name = "order")
        val order: String?,
        @Json(name = "previous_uri")
        val previousUri: Any?,
        @Json(name = "starting_after")
        val startingAfter: Any?
    )
}