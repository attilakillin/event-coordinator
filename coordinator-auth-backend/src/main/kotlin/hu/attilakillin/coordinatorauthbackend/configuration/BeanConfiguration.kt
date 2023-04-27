package hu.attilakillin.coordinatorauthbackend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Configure additional Spring Beans.
 */
@Configuration
class BeanConfiguration {

    /**
     * Password encoder bean, used to verify password signatures.
     */
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}
