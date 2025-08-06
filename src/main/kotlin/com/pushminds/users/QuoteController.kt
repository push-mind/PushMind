package com.pushminds.users

import com.pushminds.domain.dto.QuoteResponse
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
    fun getRandomQuote(@RequestHeader("X-User-Token") userToken: String): QuoteResponse {
        return quoteService.getRandomQuote(userToken)
    }
}
