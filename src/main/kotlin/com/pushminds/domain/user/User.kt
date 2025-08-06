package com.pushminds.domain.user

import com.pushminds.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val kakaoId: Long,

    var nickname: String,

    @Enumerated(EnumType.STRING)
    var role: Role,

    ) : BaseTimeEntity() {

enum class Role {
    USER, ADMIN
    }
}
