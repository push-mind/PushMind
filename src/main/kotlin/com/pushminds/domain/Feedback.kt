package com.pushminds.domain

import jakarta.persistence.*

@Entity
@Table(name = "feedback")
class Feedback(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val quoteId: Long,

    val userIdentifier: String,

    var liked: Boolean

) : BaseTimeEntity()
