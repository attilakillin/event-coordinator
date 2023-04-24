package hu.attilakillin.coordinatorarticlesbackend.services

import hu.attilakillin.coordinatorarticlesbackend.config.PropertiesConfiguration
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
     * Checks whether a given JWT token string is valid or not
     * based on its timestamps and its issuer fields.
     * To make usage easier, a null parameter is also accepted, and immediately rejected.
     */
    fun isTokenValid(token: String?): Boolean {
        if (token == null) return false

        val parser = Jwts.parserBuilder().setSigningKey(publicKey).build()
        val claims = try {
            parser.parseClaimsJws(token)
        } catch (ex: JwtException) {
            return false
        }

        val now = Date.from(Instant.now())

        return claims.body.notBefore.before(now)
            && claims.body.expiration.after(now)
            && claims.body.issuer in configuration.auth.allowedIssuers
    }
}
