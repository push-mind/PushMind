package com.pushminds.users

import com.fasterxml.jackson.databind.ObjectMapper
import com.pushminds.domain.feedback.dto.FeedbackRequest
import com.pushminds.domain.quote.Quote
import com.pushminds.domain.quote.QuoteRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FeedbackApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var quoteRepository: QuoteRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `좋아요 요청 시 201 Created를 반환하고 명언의 score가 1 증가한다`() {
        // given
        val savedQuote = quoteRepository.save(Quote(content = "테스트 명언", speaker = "테스트 연사", score = 0))
        val request = FeedbackRequest(quoteId = savedQuote.id, liked = true)

        // when & then
        mockMvc.post("/api/v1/feedback") {
            header("X-User-Identifier", "test-user")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }

        val updatedQuote = quoteRepository.findById(savedQuote.id).get()
        assert(updatedQuote.score == 1)
    }

    @Test
    fun `동일한 명언에 다시 좋아요를 요청하면 409 Conflict를 반환한다`() {
        // given
        val savedQuote = quoteRepository.save(Quote(content = "테스트 명언", speaker = "테스트 연사"))
        val request = FeedbackRequest(quoteId = savedQuote.id, liked = true)
        // 첫 번째 요청
        mockMvc.post("/api/v1/feedback") {
            header("X-User-Identifier", "test-user")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }

        // when & then : 두 번째 요청
        mockMvc.post("/api/v1/feedback") {
            header("X-User-Identifier", "test-user")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isConflict() }
        }
    }
}
