package hu.attilakillin.coordinatorauthbackend.configuration

import hu.attilakillin.coordinatorauthbackend.dal.Administrator
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * These configuration values are loaded from the application properties file(s).
 * Configure these in order to validate logins and generate secure tokens.
 */
@Configuration
@ConfigurationProperties("application")
data class PropertiesConfiguration(
    /** Set up authentication token parameters. */
    var auth: Authentication = Authentication(),
    /** Set up accepted list of administrators. */
    var admins: List<Administrator> = listOf()
) {
    /** Configuration values for generating authentication tokens. */
    data class Authentication(
        /** The private key of an RSA key pair, used to sign authentication tokens. */
        var rsaPrivateKey: String = "",
        /** The public key of an RSA key pair, used to verify existing signatures. */
        var rsaPublicKey: String = "",
        /** Sign authentication tokens in the name of this issuer. */
        var issuer: String = ""
    )
}
