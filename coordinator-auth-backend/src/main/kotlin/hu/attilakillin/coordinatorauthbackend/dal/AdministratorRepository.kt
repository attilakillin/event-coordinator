package hu.attilakillin.coordinatorauthbackend.dal

import hu.attilakillin.coordinatorauthbackend.configuration.PropertiesConfiguration
import org.springframework.stereotype.Service

/**
 * Generic repository. Used to query the list of administrators.
 * The list is read from the application properties file(s).
 */
@Service
class AdministratorRepository(
    private val configuration: PropertiesConfiguration
) {
    /**
     * Whether an administrator with the specified username exists or not.
     */
    fun existsByUsername(username: String): Boolean {
        return configuration.admins.any { it.username == username }
    }

    /**
     * Return an administrator by their username, or null, if no such admin exists.
     */
    fun findByUsernameOrNull(username: String): Administrator? {
        return configuration.admins.find { it.username == username }
    }
}
