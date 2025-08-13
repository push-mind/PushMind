package com.pushminds.admins

import com.fasterxml.jackson.databind.ObjectMapper
import com.pushminds.admins.quote.dto.QuoteCreateRequest
import com.pushminds.config.TestSecurityConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestSecurityConfig::class)
class AdminQuoteApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `인증되지 않은 사용자가 접근하면 302를 반환한다`() {
        mockMvc.get("/api/admin/quotes")
            .andExpect {
                status { isFound() }
            }
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `USER 권한으로 접근하면 403을 반환한다`() {
        mockMvc.get("/api/admin/quotes")
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `ADMIN 권한으로 접근하면 200을 반환한다`() {
        mockMvc.get("/api/admin/quotes")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `ADMIN 권한으로 명언을 등록하면 201을 반환한다`() {
        // given
        val request = QuoteCreateRequest(content = "새로운 명언", speaker = "새로운 연사")

        // when & then
        mockMvc.post("/api/admin/quotes") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }
    }
}
