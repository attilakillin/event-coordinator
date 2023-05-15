package hu.attilakillin.coordinatorparticipantsbackend.dto

/**
 * A DTO class used for receiving a new participant.
 */
data class ParticipantRequestDTO(
    /** The last name of the participant. */
    val lastName: String,
    /** The first name of the participant. */
    val firstName: String,

    /** The email of the participant. Required in order to contact them. */
    val email: String,
    /** The real-world address of the participant, not required. */
    val address: String?,
    /** The phone number of the participant, not required. */
    val phoneNumber: String?,

    /** Additional notes the participant wished to share. Not required. */
    val notes: String?
)
