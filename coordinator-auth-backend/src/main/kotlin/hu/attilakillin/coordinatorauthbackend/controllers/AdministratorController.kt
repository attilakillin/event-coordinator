package hu.attilakillin.coordinatorauthbackend.controllers

import hu.attilakillin.coordinatorauthbackend.dto.AdministratorDTO
import hu.attilakillin.coordinatorauthbackend.dto.JwtDTO
import hu.attilakillin.coordinatorauthbackend.dto.ValidationDTO
import hu.attilakillin.coordinatorauthbackend.services.AdministratorService
import io.jsonwebtoken.Claims
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class AdministratorController(
    private val service: AdministratorService
) {
    /**
     * Logging instance. All logging is done by the controller in
     * order to get as much additional information as possible.
     */
    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Login as an administrator with the given credentials.
     * Returns an HTTP 400 response if no such administrator exists,
     * or an HTTP 200 response with an authentication token if it does.
     */
    @PostMapping("/login")
    fun login(@RequestBody credentials: AdministratorDTO, req: HttpServletRequest): ResponseEntity<JwtDTO> {
        val admin = service.findAdministrator(credentials)
        if (admin == null) {
            logger.logNotAuthenticated(req, credentials.username)
            return ResponseEntity.badRequest().build()
        }

        val token = service.createTokenFor(admin)

        logger.logAuthenticated(req, admin.username)
        return ResponseEntity.ok(JwtDTO(token))
    }

    /**
     * Validates a JSON Web Token. Returns an HTTP 200 response in all cases,
     * with a boolean field in the response object that signals whether the
     * received token was valid (and created by this server) or not.
     */
    @PostMapping("/validate")
    fun validate(@RequestBody token: JwtDTO, req: HttpServletRequest): ResponseEntity<ValidationDTO> {
        val (valid, claims) = service.validateToken(token.token)
        if (claims == null) {
            logger.logTokenNotVerified(req)
        } else {
            logger.logTokenVerified(req, claims, valid)
        }

        return ResponseEntity.ok(ValidationDTO(valid))
    }
}

/* Logging extension functions. */

private val HttpServletRequest.realIp get() = getHeader("X-Real-Ip")

private fun Logger.logNotAuthenticated(req: HttpServletRequest, username: String) {
    val message = "Administrator login failed: (requested username: '{}', IP: '{}')"
    info(message, username, req.realIp)
}

private fun Logger.logAuthenticated(req: HttpServletRequest, username: String) {
    val message = "Administrator logged in: (username: '{}', IP: '{}')"
    info(message, username, req.realIp)
}

private fun Logger.logTokenNotVerified(req: HttpServletRequest) {
    val message = "Token verification failed: (IP: '{}')"
    info(message, req.realIp)
}

private fun Logger.logTokenVerified(req: HttpServletRequest, claims: Claims, valid: Boolean) {
    val message = "Token validated: (subject: '{}', valid: '{}' , IP '{}')"
    info(message, claims.subject, valid, req.realIp)
}
