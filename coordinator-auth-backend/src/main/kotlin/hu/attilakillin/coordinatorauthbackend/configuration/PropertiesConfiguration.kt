package hu.attilakillin.coordinatorauthbackend.configuration

import hu.attilakillin.coordinatorauthbackend.dal.Administrator
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("application")
data class PropertiesConfiguration(
    var crossOrigin: String = "",
    var jwt: Jwt = Jwt(),
    var admins: List<Administrator> = listOf()
) {
    data class Jwt(
        var privateKey: String = "",
        var publicKey: String = "",
        var issuer: String = ""
    )
}
