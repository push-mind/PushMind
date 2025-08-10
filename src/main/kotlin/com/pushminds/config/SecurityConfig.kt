package com.pushminds.config

import com.pushminds.users.auth.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { it.frameOptions { it.disable() } } // for h2-console
            .authorizeHttpRequests {
                it.requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                it.requestMatchers("/api/v1/**").hasRole("USER")
                it.requestMatchers("/api/admin/**").hasRole("ADMIN")
                it.anyRequest().authenticated()
            }
            .logout { it.logoutSuccessUrl("/") }
            .oauth2Login {
                it.userInfoEndpoint {
                    it.userService(customOAuth2UserService)
                }
            }

        return http.build()
    }
}
