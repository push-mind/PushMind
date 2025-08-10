package com.pushminds.users.auth.service

import com.pushminds.domain.user.User
import com.pushminds.domain.user.UserRepository
import com.pushminds.users.auth.dto.OAuthAttributes
import jakarta.servlet.http.HttpSession
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
    private val httpSession: HttpSession
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val registrationId = userRequest.clientRegistration.registrationId
        val userNameAttributeName = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName

        val attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.attributes)

        val user = saveOrUpdate(attributes)

        httpSession.setAttribute("user", user) // SessionUser(user) DTO를 만들어 사용하는 것이 좋음

        return DefaultOAuth2User(
            setOf(SimpleGrantedAuthority(user.role.key)),
            attributes.attributes,
            attributes.nameAttributeKey
        )
    }

    private fun saveOrUpdate(attributes: OAuthAttributes): User {
        val user = userRepository.findByEmail(attributes.email)
            ?.update(attributes.name, attributes.picture)
            ?: attributes.toEntity()

        return userRepository.save(user)
    }
}
