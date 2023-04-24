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

@Service
class JwtService(
    private val configuration: PropertiesConfiguration
) {
    private val publicKey: PublicKey
    init {
        val factory = KeyFactory.getInstance("RSA")

        val publicKeyContent = configuration.auth.publicKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")

        publicKey = factory.generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent)))
    }

    fun validateToken(token: String?): Boolean {
        if (token == null) return false

        val claims = try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
        } catch (ex: JwtException) {
            return false
        }

        val now = Date.from(Instant.now())

        return claims.body.notBefore.before(now)
                && claims.body.expiration.after(now)
                && claims.body.issuer == configuration.auth.issuer
    }
}
