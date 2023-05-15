package hu.attilakillin.coordinatorparticipantsbackend.services

import hu.attilakillin.coordinatorparticipantsbackend.config.PropertiesConfiguration
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.*

/**
 * Performs tasks related to authentication.
 */
@Service
class AuthService(
    private val configuration: PropertiesConfiguration
) {
    /**
     * The public key related to the RSA private key that was used to sign the authentication tokens.
     * Read from a configuration file in a string format, and parsed here, during initialization.
     */
    private val publicKey: PublicKey

    init {
        val keyContent = configuration.auth.rsaPublicKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")

        publicKey = KeyFactory.getInstance("RSA")
            .generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(keyContent)))
    }

    /**
     * Returns the verified list of claims of a JWT token.
     * Returns null if the token is null, invalid, or wasn't signed with the proper key.
     *
     * This method does not care about validity - it only verifies that the token was
     * properly signed, and is in the correct format.
     */
    fun getVerifiedClaims(token: String?): Claims? {
        if (token == null) {
            return null
        }

        val parser = Jwts.parserBuilder().setSigningKey(publicKey).build()
        return try {
            parser.parseClaimsJws(token).body
        } catch (ex: JwtException) {
            null
        }
    }

    /**
     * Checks whether a given JWT token string is valid or not
     * based on its timestamps and its issuer fields.
     */
    fun validateClaims(claims: Claims): Boolean {
        val now = Date.from(Instant.now())
        return claims.notBefore.before(now) && claims.expiration.after(now)
            && claims.issuer in configuration.auth.allowedIssuers
    }

    /**
     * Verifies and validates a given token. A null parameter is considered invalid.
     */
    fun isTokenValid(token: String?): Boolean {
        val claims = getVerifiedClaims(token) ?: return false
        return validateClaims(claims)
    }
}
