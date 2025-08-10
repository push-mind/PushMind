package com.pushminds.domain.quote

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface QuoteRepository : JpaRepository<Quote, Long> {
    fun findByOrderByScoreDesc(pageable: Pageable): List<Quote>
}
