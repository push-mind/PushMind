package com.pushminds.admins.quote.service

import com.pushminds.admins.quote.dto.QuoteCreateRequest
import com.pushminds.admins.quote.dto.QuoteResponse
import com.pushminds.domain.quote.Quote
import com.pushminds.domain.quote.QuoteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminQuoteService(
    private val quoteRepository: QuoteRepository
) {
    fun createQuote(request: QuoteCreateRequest): QuoteResponse {
        val quote = quoteRepository.save(
            Quote(
                content = request.content,
                speaker = request.speaker
            )
        )
        return QuoteResponse(
            id = quote.id,
            content = quote.content,
            speaker = quote.speaker
        )
    }
}
