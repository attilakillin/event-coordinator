package hu.attilakillin.coordinatorarticlesbackend.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("application")
data class PropertiesConfiguration(
    var crossOrigin: String = "",
    var auth: Auth = Auth()
) {
    data class Auth(
        var publicKey: String = "",
        var issuer: String = ""
    )
}
