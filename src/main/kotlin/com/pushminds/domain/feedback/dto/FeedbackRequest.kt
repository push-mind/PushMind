package com.pushminds.domain.feedback.dto

data class FeedbackRequest(
    val quoteId: Long,
    val liked: Boolean
)
