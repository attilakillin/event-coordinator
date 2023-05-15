package hu.attilakillin.coordinatorparticipantsbackend.dto

import hu.attilakillin.coordinatorparticipantsbackend.dal.Participant

/**
 * A DTO class used for sending information related to a participant.
 */
data class ParticipantResponseDTO(
    /** Unique ID, used internally, not shown to the end user. */
    val id: Long,

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

/**
 * Convert a participant entity to a DTO containing the relevant information.
 */
fun Participant.toDTO(): ParticipantResponseDTO {
    return ParticipantResponseDTO(
        id = this.id,
        lastName = this.lastName,
        firstName = this.firstName,
        email = this.email,
        address = this.address,
        phoneNumber = this.phoneNumber,
        notes = this.notes
    )
}
