package com.pushminds.users

import com.pushminds.config.TestSecurityConfig
import com.pushminds.domain.feedback.Feedback
import com.pushminds.domain.feedback.FeedbackRepository
import com.pushminds.domain.quote.Quote
import com.pushminds.domain.quote.QuoteRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = ["USER"])
@Import(TestSecurityConfig::class)
class QuoteApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var quoteRepository: QuoteRepository

    @Autowired
    private lateinit var feedbackRepository: FeedbackRepository

    @Test
    fun `랜덤 명언 조회 API는 200 OK와 명언 정보를 반환한다`() {
        // given
        quoteRepository.save(Quote(content = "테스트 명언", speaker = "테스트 연사"))

        // when & then
        mockMvc.get("/api/v1/quotes/random") {
            header("X-User-Identifier", "test-user-identifier")
        }.andExpect {
            status {
                isOk()
            }
            jsonPath("$.id") { isNotEmpty() }
            jsonPath("$.content") { value("테스트 명언") }
            jsonPath("$.speaker") { value("테스트 연사") }
        }
    }

    @Test
    fun `피드백을 남긴 명언은 추천에서 제외된다`() {
        // given
        val quote1 = quoteRepository.save(Quote(content = "명언 1", speaker = "연사 1"))
        val quote2 = quoteRepository.save(Quote(content = "명언 2", speaker = "연사 2"))
        feedbackRepository.save(Feedback(quoteId = quote1.id, userIdentifier = "test-user", liked = true))

        // when & then
        // 여러 번 호출해도 quote2만 반환되어야 함
        repeat(10) {
            mockMvc.get("/api/v1/quotes/random") {
                header("X-User-Identifier", "test-user")
            }.andExpect {
                status { isOk() }
                jsonPath("$.content") { value("명언 2") }
            }
        }
    }

    @Test
    fun `인기 명언 조회 API는 score 순으로 정렬된 명언 목록을 반환한다`() {
        // given
        val quote1 = quoteRepository.save(Quote(content = "명언 1", speaker = "연사 1", score = 10))
        val quote2 = quoteRepository.save(Quote(content = "명언 2", speaker = "연사 2", score = 20))
        val quote3 = quoteRepository.save(Quote(content = "명언 3", speaker = "연사 3", score = 5))

        // when & then
        mockMvc.get("/api/v1/quotes/top")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].content") { value(quote2.content) }
                jsonPath("$[1].content") { value(quote1.content) }
                jsonPath("$[2].content") { value(quote3.content) }
            }
    }

    @Test
    fun `인기 명언 조회 API는 limit 파라미터로 개수를 제한할 수 있다`() {
        // given
        quoteRepository.save(Quote(content = "명언 1", speaker = "연사 1", score = 10))
        quoteRepository.save(Quote(content = "명언 2", speaker = "연사 2", score = 20))
        quoteRepository.save(Quote(content = "명언 3", speaker = "연사 3", score = 5))

        // when & then
        mockMvc.get("/api/v1/quotes/top?limit=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(2) }
                jsonPath("$[0].content") { value("명언 2") } // score 20
            }
    }
}
