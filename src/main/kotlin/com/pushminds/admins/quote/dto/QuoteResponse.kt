package com.pushminds.admins.quote.dto

data class QuoteResponse(
    val id: Long,
    val content: String,
    val speaker: String
)