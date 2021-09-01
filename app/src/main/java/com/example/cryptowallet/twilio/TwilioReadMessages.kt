package com.example.cryptowallet.twilio

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TwilioReadMessages(
    @Json(name = "end")
    val end: Int?,
    @Json(name = "first_page_uri")
    val firstPageUri: String?,
    @Json(name = "messages")
    val messages: List<Message?>?,
    @Json(name = "next_page_uri")
    val nextPageUri: String?,
    @Json(name = "page")
    val page: Int?,
    @Json(name = "page_size")
    val pageSize: Int?,
    @Json(name = "previous_page_uri")
    val previousPageUri: String?,
    @Json(name = "start")
    val start: Int?,
    @Json(name = "uri")
    val uri: String?
) {
    @JsonClass(generateAdapter = true)
    data class Message(
        @Json(name = "account_sid")
        val accountSid: String?,
        @Json(name = "api_version")
        val apiVersion: String?,
        @Json(name = "body")
        val body: String?,
        @Json(name = "date_created")
        val dateCreated: String?,
        @Json(name = "date_sent")
        val dateSent: String?,
        @Json(name = "date_updated")
        val dateUpdated: String?,
        @Json(name = "direction")
        val direction: String?,
        @Json(name = "error_code")
        val errorCode: Any?,
        @Json(name = "error_message")
        val errorMessage: Any?,
        @Json(name = "from")
        val from: String?,
        @Json(name = "messaging_service_sid")
        val messagingServiceSid: Any?,
        @Json(name = "num_media")
        val numMedia: String?,
        @Json(name = "num_segments")
        val numSegments: String?,
        @Json(name = "price")
        val price: String?,
        @Json(name = "price_unit")
        val priceUnit: String?,
        @Json(name = "sid")
        val sid: String?,
        @Json(name = "status")
        val status: String?,
        @Json(name = "subresource_uris")
        val subresourceUris: SubresourceUris?,
        @Json(name = "to")
        val to: String?,
        @Json(name = "uri")
        val uri: String?
    ) {
        @JsonClass(generateAdapter = true)
        data class SubresourceUris(
            @Json(name = "feedback")
            val feedback: String?,
            @Json(name = "media")
            val media: String?
        )
    }
}