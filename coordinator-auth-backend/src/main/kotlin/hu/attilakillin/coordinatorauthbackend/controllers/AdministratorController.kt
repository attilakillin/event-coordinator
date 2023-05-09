package hu.attilakillin.coordinatorauthbackend.controllers

import hu.attilakillin.coordinatorauthbackend.dto.AdministratorDTO
import hu.attilakillin.coordinatorauthbackend.dto.JwtDTO
import hu.attilakillin.coordinatorauthbackend.dto.ValidationDTO
import hu.attilakillin.coordinatorauthbackend.services.AdministratorService
import jakarta.servlet.http.HttpServletRequest
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
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val messages = object {
        val failedLogin = "Administrator login failed: (requested username: '{}', IP: '{}')"
        val passedLogin = "Administrator logged in: (username: '{}', IP: '{}')"

        val failedToken = "Token verification failed: (IP: '{}')"
        val passedToken = "Token validated: (subject: '{}', IP '{}', valid: '{}')"
    }

    /**
     * Login as an administrator with the given credentials.
     * Returns an HTTP 400 response if no such administrator exists,
     * or an HTTP 200 response with an authentication token if it does.
     */
    @PostMapping("/login")
    fun login(@RequestBody credentials: AdministratorDTO, req: HttpServletRequest): ResponseEntity<JwtDTO> {
        val admin = service.findAdministrator(credentials)
        if (admin == null) {
            logger.info(messages.failedLogin, credentials.username, req.remoteAddr)
            return ResponseEntity.badRequest().build()
        }

        val token = service.createTokenFor(admin)

        logger.info(messages.passedLogin, admin.username, req.remoteAddr)
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
            logger.info(messages.failedToken, req.remoteAddr)
        } else {
            logger.info(messages.passedToken, claims.subject, req.remoteAddr, valid)
        }

        return ResponseEntity.ok(ValidationDTO(valid))
    }
}
