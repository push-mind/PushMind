package com.pushminds.admins.quote.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/quotes")
class AdminQuoteController {

    @GetMapping
    fun getQuotes(): String {
        return "OK"
    }
}
