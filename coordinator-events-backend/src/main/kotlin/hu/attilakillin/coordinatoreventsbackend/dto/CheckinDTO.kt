package hu.attilakillin.coordinatoreventsbackend.dto

import hu.attilakillin.coordinatoreventsbackend.dal.CheckinStatus
import hu.attilakillin.coordinatoreventsbackend.dal.Event

/**
 * A DTO class for communicating check-in details over WebSocket.
 */
data class CheckinDTO (
    /** The ID of the event to modify. */
    val eventId: Long,
    /** The email of the participant we have the status of. */
    val email: String,
    /** The check-in status of this participant. */
    val status: String
)

/**
 * Convert an event to a list of check-in DTOs containing the list
 * of participant emails and their check-in status.
 */
fun Event.toCheckinDTOList(): List<CheckinDTO> {
    return this.participants.map {
        CheckinDTO(
            eventId = this.id,
            email = it.key,
            status = it.value.toString()
        )
    }
}

/**
 * Returns the status as a valid enum value of the check-in DTO.
 */
fun CheckinDTO.getValidStatus(): CheckinStatus? {
    return when (this.status.lowercase()) {
        "unknown" -> CheckinStatus.UNKNOWN
        "checked_in" -> CheckinStatus.CHECKED_IN
        "declined" -> CheckinStatus.DECLINED
        else -> null
    }
}
