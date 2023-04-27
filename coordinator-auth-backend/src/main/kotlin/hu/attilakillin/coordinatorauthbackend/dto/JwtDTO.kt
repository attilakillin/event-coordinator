package hu.attilakillin.coordinatorauthbackend.dto

/**
 * A DTO containing the authentication token in a member field.
 */
data class JwtDTO(
    /** The Base64 encoded, three-part JWT token used for authentication. */
    val token: String
)
