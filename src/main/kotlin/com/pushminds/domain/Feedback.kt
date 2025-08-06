package com.pushminds.domain

import com.pushminds.domain.quote.Quote
import jakarta.persistence.*

@Entity
@Table(name = "feedback")
class Feedback(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    val quote: Quote,

    val userToken: String,

    var liked: Boolean

) : BaseTimeEntity()
