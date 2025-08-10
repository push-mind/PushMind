package com.pushminds.domain.user

import com.pushminds.domain.BaseTimeEntity
import com.pushminds.users.auth.dto.Role
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,

    var email: String,

    var picture: String,

    @Enumerated(EnumType.STRING)
    var role: Role,
) : BaseTimeEntity() {
    fun update(name: String, picture: String): User {
        this.name = name
        this.picture = picture
        return this
    }
}
