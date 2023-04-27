package hu.attilakillin.coordinatorauthbackend.services

import hu.attilakillin.coordinatorauthbackend.configuration.PropertiesConfiguration
import hu.attilakillin.coordinatorauthbackend.dal.Administrator
import hu.attilakillin.coordinatorauthbackend.dal.AdministratorRepository
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.*

/**
 * Performs tasks related to authentication.
 */
@Service
class AuthService(
    private val configuration: PropertiesConfiguration,
    private val repository: AdministratorRepository
) {
    /**
     * The public key related to an RSA private key. Used to verify authentication tokens.
     * Read from a configuration file in a string format, and parsed here, during initialization.
     */
    private val privateKey: PrivateKey

    /**
     * The private key related to an RSA public key. Used to sign authentication tokens.
     * Read from a configuration file in a string format, and parsed here, during initialization.
     */
    private val publicKey: PublicKey
    init {
        val privateKeyContent = configuration.auth.rsaPrivateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\n", "")
        val publicKeyContent = configuration.auth.rsaPublicKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")

        val factory = KeyFactory.getInstance("RSA")
        val decoder = Base64.getDecoder()
        privateKey = factory.generatePrivate(PKCS8EncodedKeySpec(decoder.decode(privateKeyContent)))
        publicKey = factory.generatePublic(X509EncodedKeySpec(decoder.decode(publicKeyContent)))
    }

    /**
     * Creates a token for the given administrator with the given validity lifespan (in seconds).
     * The token is valid from the moment this method is called.
     */
    fun createTokenFor(user: Administrator, lifespan: Long): String {
        val now = Instant.now()

        return Jwts.builder()
            .setHeader(mapOf("typ" to "JWT", "alg" to "RS256"))
            .setIssuedAt(Date.from(now))
            .setNotBefore(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(lifespan)))
            .setIssuer(configuration.auth.issuer)
            .setSubject(user.username)
            .signWith(privateKey)
            .compact()
    }

    /**
     * Validates A JSON Web Token. The token is valid if the timestamp are correct, the token
     * hasn't expired yet, the issuer matches, and an administrator with the given username
     * exists.
     */
    fun validateToken(token: String): Boolean {
        val claims = try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
        } catch (ex: JwtException) {
            return false
        }

        val now = Date.from(Instant.now())

        return claims.body.issuedAt.before(now)
            && claims.body.notBefore.before(now)
            && claims.body.expiration.after(now)
            && claims.body.issuer.equals(configuration.auth.issuer)
            && repository.existsByUsername(claims.body.subject)
    }
}
