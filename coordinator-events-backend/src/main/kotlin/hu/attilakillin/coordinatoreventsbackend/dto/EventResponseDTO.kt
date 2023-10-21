package hu.attilakillin.coordinatoreventsbackend.dto

import hu.attilakillin.coordinatoreventsbackend.dal.Event
import java.time.OffsetDateTime

/**
 * A DTO class used for sending detailed information about an event.
 */
data class EventResponseDTO(
    /** The moment an event was created. */
    val created: OffsetDateTime,

    /** The title of the event. */
    val title: String,

    /** The list of registered participant emails to this event */
    var participants: List<String>
)

/**
 * Convert an event to a DTO containing its title and its participant list.
 */
fun Event.toDTO(): EventResponseDTO {
    return EventResponseDTO(
        created = this.created,
        title = this.title,
        participants = this.participants.map { it.key }
    )
}
