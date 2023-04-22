package hu.attilakillin.coordinatorauthbackend.services

import hu.attilakillin.coordinatorauthbackend.dal.Administrator
import hu.attilakillin.coordinatorauthbackend.dal.AdministratorRepository
import hu.attilakillin.coordinatorauthbackend.dto.AdministratorDTO
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AdministratorService(
    private val repository: AdministratorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    fun findAdministrator(admin: AdministratorDTO): Administrator? {
        val candidate = repository.findByUsernameOrNull(admin.username) ?: return null

        return if (passwordEncoder.matches(admin.password, candidate.password)) {
            candidate
        } else {
            null
        }
    }

    fun createTokenFor(admin: Administrator): String? {
        if (!repository.existsByUsername(admin.username)) return null

        return jwtService.createTokenFor(admin, 1800)
    }

    fun validateToken(token: String): Boolean {
        return jwtService.validateToken(token)
    }
}
