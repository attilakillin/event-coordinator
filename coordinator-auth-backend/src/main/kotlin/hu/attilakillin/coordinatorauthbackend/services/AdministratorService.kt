package hu.attilakillin.coordinatorauthbackend.services

import hu.attilakillin.coordinatorauthbackend.dal.Administrator
import hu.attilakillin.coordinatorauthbackend.dal.AdministratorRepository
import hu.attilakillin.coordinatorauthbackend.dto.AdministratorDTO
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Performs tasks related to administrator management and token verification.
 * Technically, this service class is the heart of the application.
 */
@Service
class AdministratorService(
    private val repository: AdministratorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authService: AuthService
) {
    /**
     * Parse the received credentials, and return a matching administrator entity,
     * or null, if no such entity is present.
     */
    fun findAdministrator(admin: AdministratorDTO): Administrator? {
        val candidate = repository.findByUsernameOrNull(admin.username) ?: return null

        return if (passwordEncoder.matches(admin.password, candidate.password)) {
            candidate
        } else {
            null
        }
    }

    /**
     * Create a token for the given administrator.
     */
    fun createTokenFor(admin: Administrator): String? {
        if (!repository.existsByUsername(admin.username)) return null

        return authService.createTokenFor(admin, 1800)
    }

    /**
     * Validate an encoded JSON Web Token.
     */
    fun validateToken(token: String): Boolean {
        return authService.validateToken(token)
    }
}
