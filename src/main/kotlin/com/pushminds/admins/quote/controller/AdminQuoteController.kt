package com.pushminds.admins.quote.controller

import com.pushminds.admins.quote.dto.QuoteCreateRequest
import com.pushminds.admins.quote.dto.QuoteResponse
import com.pushminds.admins.quote.service.AdminQuoteService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/quotes")
class AdminQuoteController(
    private val adminQuoteService: AdminQuoteService
) {

    @GetMapping
    fun getQuotes(): String {
        return "OK"
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createQuotes(
        @RequestBody request: QuoteCreateRequest
    ): QuoteResponse {
        return adminQuoteService.createQuote(request)
    }
}
