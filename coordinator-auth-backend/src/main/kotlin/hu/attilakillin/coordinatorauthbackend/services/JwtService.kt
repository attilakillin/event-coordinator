package hu.attilakillin.coordinatorauthbackend.services

import hu.attilakillin.coordinatorauthbackend.configuration.PropertiesConfiguration
import hu.attilakillin.coordinatorauthbackend.dal.Administrator
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class JwtService(
    private val configuration: PropertiesConfiguration
) {
    fun createTokenFor(user: Administrator, lifespan: Long): String {
        val now = Instant.now()

        return Jwts.builder()
            .setHeader(mapOf("typ" to "JWT", "alg" to "TODO"))
            .setIssuedAt(Date.from(now))
            .setNotBefore(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(lifespan)))
            .setIssuer(configuration.jwt.issuer)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        // TODO
        return false
    }
}
