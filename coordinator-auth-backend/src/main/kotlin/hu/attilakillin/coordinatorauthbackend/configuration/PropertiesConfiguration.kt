package hu.attilakillin.coordinatorauthbackend.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("application")
data class PropertiesConfiguration(
    var crossOrigin: String = "",
    var jwt: Jwt = Jwt()
) {
    data class Jwt(
        var privateKey: String = "",
        var publicKey: String = "",
        var issuer: String = ""
    )
}
