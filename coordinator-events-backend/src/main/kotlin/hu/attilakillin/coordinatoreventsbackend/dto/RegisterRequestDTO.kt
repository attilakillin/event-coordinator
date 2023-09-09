package hu.attilakillin.coordinatoreventsbackend.dto

/**
 * A DTO class used for receiving an email-to-event registration request.
 */
data class RegisterRequestDTO(
    /** The email to register. */
    val email: String
)
