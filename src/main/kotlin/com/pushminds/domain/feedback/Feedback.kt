package com.pushminds.domain.feedback

import com.pushminds.domain.BaseTimeEntity
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