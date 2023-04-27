package hu.attilakillin.coordinatorauthbackend.dto

/**
 * A DTO class used for responding to validation queries.
 */
data class ValidationDTO(
    /** Whether the requested token was valid or not. */
    val valid: Boolean
)
