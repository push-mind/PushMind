package com.pushminds.users.quote.service

import com.pushminds.domain.feedback.FeedbackRepository
import com.pushminds.domain.quote.dto.QuoteResponse
import com.pushminds.domain.quote.QuoteRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuoteService(
    private val quoteRepository: QuoteRepository,
    private val feedbackRepository: FeedbackRepository
) {

    @Transactional(readOnly = true)
    fun getRandomQuote(userIdentifier: String): QuoteResponse {
        val feedbackGivenQuoteIds = feedbackRepository.findAllByUserIdentifier(userIdentifier)
            .map { it.quoteId }

        val quote = if (feedbackGivenQuoteIds.isEmpty()) {
            quoteRepository.findAll().random()
        } else {
            quoteRepository.findAll().filter { it.id !in feedbackGivenQuoteIds }.random()
        }

        return QuoteResponse(
            id = quote.id,
            content = quote.content,
            speaker = quote.speaker,
            score = quote.score
        )
    }

    @Transactional(readOnly = true)
    fun getTopQuotes(limit: Int): List<QuoteResponse> {
        return quoteRepository.findByOrderByScoreDesc(PageRequest.of(0, limit)).map {
            QuoteResponse(
                id = it.id,
                content = it.content,
                speaker = it.speaker,
                score = it.score
            )
        }
    }
}
