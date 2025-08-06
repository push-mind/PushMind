package com.pushminds.domain.feedback

import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository : JpaRepository<Feedback, Long> {
    fun findByUserIdentifierAndQuoteId(userIdentifier: String, quoteId: Long): Feedback?
    fun findAllByUserIdentifier(userIdentifier: String): List<Feedback>
}