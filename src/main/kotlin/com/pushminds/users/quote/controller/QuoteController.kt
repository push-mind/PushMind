package com.pushminds.users.quote.controller

import com.pushminds.domain.quote.dto.QuoteResponse
import com.pushminds.users.quote.service.QuoteService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/quotes")
class QuoteController(
    private val quoteService: QuoteService
) {

    @GetMapping("/random")
    fun getRandomQuote(@RequestHeader("X-User-Identifier") userIdentifier: String): QuoteResponse {
        return quoteService.getRandomQuote(userIdentifier)
    }
}
