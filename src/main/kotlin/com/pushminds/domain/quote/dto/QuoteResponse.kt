package com.pushminds.domain.quote.dto

data class QuoteResponse(
    val id: Long,
    val content: String,
    val speaker: String,
    val score: Int
)
