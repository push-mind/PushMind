package com.pushminds.domain.quote

import com.pushminds.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "quotes")
class Quote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 1000)
    var content: String,

    var speaker: String,

    var score: Int = 0

) : BaseTimeEntity()
