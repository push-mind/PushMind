package com.pushminds.users.feedback.controller

import com.pushminds.domain.feedback.dto.FeedbackRequest
import com.pushminds.users.feedback.service.FeedbackService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/feedback")
class FeedbackController(
    private val feedbackService: FeedbackService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postFeedback(
        @RequestHeader("X-User-Identifier") userIdentifier: String,
        @RequestBody request: FeedbackRequest
    ) {
        feedbackService.processFeedback(userIdentifier, request)
    }
}
