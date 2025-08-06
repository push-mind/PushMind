package com.pushminds.domain

import com.pushminds.domain.quote.Quote
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository : JpaRepository<Feedback, Long> {
    fun findByUserTokenAndQuote(userToken: String, quote: Quote): Feedback?
    fun findAllByUserToken(userToken: String): List<Feedback>
}
