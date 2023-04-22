package hu.attilakillin.coordinatorauthbackend.dal

import hu.attilakillin.coordinatorauthbackend.configuration.PropertiesConfiguration
import org.springframework.stereotype.Service

@Service
class AdministratorRepository(
    private val configuration: PropertiesConfiguration
) {
    fun existsByUsername(username: String): Boolean {
        return configuration.admins.any { it.username == username }
    }
    fun findByUsernameOrNull(username: String): Administrator? {
        return configuration.admins.find { it.username == username }
    }
}
