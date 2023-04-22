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

@Service
class JwtService(
    private val configuration: PropertiesConfiguration,
    private val repository: AdministratorRepository
) {
    private val privateKey: PrivateKey
    private val publicKey: PublicKey
    init {
        val factory = KeyFactory.getInstance("RSA")

        val privateKeyContent = configuration.jwt.privateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\n", "")
        val publicKeyContent = configuration.jwt.publicKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")

        privateKey = factory.generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent)))
        publicKey = factory.generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent)))
    }

    fun createTokenFor(user: Administrator, lifespan: Long): String {
        val now = Instant.now()

        return Jwts.builder()
            .setHeader(mapOf("typ" to "JWT", "alg" to "RS256"))
            .setIssuedAt(Date.from(now))
            .setNotBefore(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(lifespan)))
            .setIssuer(configuration.jwt.issuer)
            .setSubject(user.username)
            .signWith(privateKey)
            .compact()
    }

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

        return claims.body.notBefore.before(now)
            && claims.body.expiration.after(now)
            && repository.existsByUsername(claims.body.subject)
    }
}
