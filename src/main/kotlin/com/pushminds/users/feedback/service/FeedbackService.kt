package com.pushminds.users.feedback.service

import com.pushminds.domain.feedback.Feedback
import com.pushminds.domain.feedback.FeedbackRepository
import com.pushminds.domain.feedback.dto.FeedbackRequest
import com.pushminds.domain.quote.QuoteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
    private val quoteRepository: QuoteRepository
) {

    @Transactional
    fun processFeedback(userIdentifier: String, request: FeedbackRequest) {
        // 중복 피드백 확인
        feedbackRepository.findByUserIdentifierAndQuoteId(userIdentifier, request.quoteId)?.let {
            throw IllegalArgumentException("Already feedback given") // 예외 처리는 추후에 구체화
        }

        val quote = quoteRepository.findById(request.quoteId).orElseThrow()
        quote.score += 1 // 우선 단순 증가 로직만 구현

        feedbackRepository.save(
            Feedback(
                quoteId = request.quoteId,
                userIdentifier = userIdentifier,
                liked = request.liked
            )
        )
    }
}
