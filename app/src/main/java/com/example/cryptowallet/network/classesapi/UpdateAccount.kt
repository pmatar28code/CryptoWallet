package com.example.cryptowallet.network.classesapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateAccount(
    @Json(name = "data")
    val data: Data?
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "balance")
        val balance: Balance?,
        @Json(name = "created_at")
        val createdAt: String?,
        @Json(name = "currency")
        val currency: String?,
        @Json(name = "id")
        val id: String?,
        @Json(name = "name")
        val name: String?,
        @Json(name = "primary")
        val primary: Boolean?,
        @Json(name = "resource")
        val resource: String?,
        @Json(name = "resource_path")
        val resourcePath: String?,
        @Json(name = "type")
        val type: String?,
        @Json(name = "updated_at")
        val updatedAt: String?
    ) {
        @JsonClass(generateAdapter = true)
        data class Balance(
            @Json(name = "amount")
            val amount: String?,
            @Json(name = "currency")
            val currency: String?
        )
    }
}