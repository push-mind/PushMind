package com.pushminds.users.auth.dto

import com.pushminds.domain.user.User

enum class Role(val key: String, val title: String) {
    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자")
}

data class OAuthAttributes(
    val attributes: Map<String, Any>,
    val nameAttributeKey: String,
    val name: String,
    val email: String,
    val picture: String
) {
    fun toEntity(): User {
        return User(
            name = name,
            email = email,
            picture = picture,
            role = Role.USER
        )
    }

    companion object {
        fun of(registrationId: String, userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return when (registrationId) {
                "kakao" -> ofKakao("id", attributes)
                else -> throw IllegalArgumentException("Unsupported registrationId: $registrationId")
            }
        }

        private fun ofKakao(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            val kakaoAccount = attributes["kakao_account"] as Map<String, Any>
            val profile = kakaoAccount["profile"] as Map<String, Any>

            return OAuthAttributes(
                name = profile["nickname"] as String,
                email = kakaoAccount["email"] as String,
                picture = profile["profile_image_url"] as String,
                attributes = attributes,
                nameAttributeKey = userNameAttributeName
            )
        }
    }
}
