package hu.attilakillin.coordinatorauthbackend.dto

/**
 * A DTO class used for receiving the credentials of an administrator.
 */
data class AdministratorDTO(
    /** Username of the administrator. */
    var username: String,
    /** Password of the administrator. */
    var password: String
)
