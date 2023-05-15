package hu.attilakillin.coordinatorparticipantsbackend.dal

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

/**
 * The main domain specific entity of the application:
 * An event participant, and their relevant information.
 */
@Entity(name = "participants")
data class Participant(
    /** Unique ID, used internally, not shown to the end user. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /** The last name of the participant. */
    var lastName: String,
    /** The first name of the participant. */
    var firstName: String,

    /** The email of the participant. Required in order to contact them. */
    var email: String,
    /** The real-world address of the participant, not required. */
    var address: String?,
    /** The phone number of the participant, not required. */
    var phoneNumber: String?,

    /** Additional notes the participant wished to share. Not required. */
    var notes: String?
)
