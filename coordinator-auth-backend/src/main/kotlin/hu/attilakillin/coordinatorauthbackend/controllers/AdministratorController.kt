package hu.attilakillin.coordinatorauthbackend.controllers

import hu.attilakillin.coordinatorauthbackend.dto.AdministratorDTO
import hu.attilakillin.coordinatorauthbackend.dto.JwtDTO
import hu.attilakillin.coordinatorauthbackend.dto.ValidationDTO
import hu.attilakillin.coordinatorauthbackend.services.AdministratorService
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
     * Login as an administrator with the given credentials.
     * Returns an HTTP 400 response if no such administrator exists,
     * or an HTTP 200 response with an authentication token if it does.
     */
    @PostMapping("/login")
    fun login(@RequestBody credentials: AdministratorDTO): ResponseEntity<JwtDTO> {
        val admin = service.findAdministrator(credentials)
            ?: return ResponseEntity.badRequest().build()

        val token = service.createTokenFor(admin)
            ?: return ResponseEntity.badRequest().build()

        return ResponseEntity.ok(JwtDTO(token))
    }

    /**
     * Validates a JSON Web Token. Returns an HTTP 200 response in all cases,
     * with a boolean field in the response object that signals whether the
     * received token was valid (and created by this server) or not.
     */
    @PostMapping("/validate")
    fun validate(@RequestBody token: JwtDTO): ResponseEntity<ValidationDTO> {
        val valid = service.validateToken(token.token)

        return ResponseEntity.ok(ValidationDTO(valid))
    }
}
