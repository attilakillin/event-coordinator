package hu.attilakillin.coordinatorauthbackend

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
@CrossOrigin(origins = ["#{propertiesConfiguration.crossOrigin}"])
class AdministratorController(
    private val service: AdministratorService
) {
    @PostMapping("/register")
    fun register(@RequestBody credentials: AdministratorDTO): ResponseEntity<JwtDTO> {
        val admin = service.saveAdministrator(credentials)
            ?: return ResponseEntity.badRequest().build()

        val token = service.createTokenFor(admin)
            ?: return ResponseEntity.badRequest().build()

        return ResponseEntity.ok(JwtDTO(token))
    }

    @PostMapping("/login")
    fun login(@RequestBody credentials: AdministratorDTO): ResponseEntity<JwtDTO> {
        val admin = service.findAdministrator(credentials)
            ?: return ResponseEntity.badRequest().build()

        val token = service.createTokenFor(admin)
            ?: return ResponseEntity.badRequest().build()

        return ResponseEntity.ok(JwtDTO(token))
    }

    @PostMapping("/validate")
    fun validate(@RequestBody token: JwtDTO): ResponseEntity<ValidationDTO> {
        val valid = service.validateToken(token.token)

        return ResponseEntity.ok(ValidationDTO(valid))
    }
}
