package hu.attilakillin.coordinatoreventsbackend.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * These configuration values are loaded from the application properties file(s).
 * Configure these in order to accept and verify incoming requests.
 */
@Configuration
@ConfigurationProperties("application")
data class PropertiesConfiguration(
    /** Set up authentication parameters. */
    var auth: Authentication = Authentication(),
    /** The URL and endpoint to use for verifying that an email exists. */
    var emailVerificationUrl: String = ""
) {
    /** Configuration values for verifying incoming requests. */
    data class Authentication(
        /** The public key of an RSA key pair, where the corresponding
         *  private key was used to sign the received authentication token. */
        var rsaPublicKey: String = "",
        /** Only accept JWT authentication tokens issued by the following source(s). */
        var allowedIssuers: List<String> = listOf()
    )
}
