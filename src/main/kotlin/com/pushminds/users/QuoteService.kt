package com.pushminds.users

import com.pushminds.domain.quote.QuoteRepository
import com.pushminds.domain.dto.QuoteResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuoteService(
    private val quoteRepository: QuoteRepository
) {

    @Transactional(readOnly = true)
    fun getRandomQuote(userToken: String): QuoteResponse {
        // TODO: 알고리즘 구현 필요
        val quote = quoteRepository.findAll().first()
        return QuoteResponse(
            id = quote.id,
            content = quote.content,
            speaker = quote.speaker
        )
    }
}
