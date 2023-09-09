package hu.attilakillin.coordinatoreventsbackend.dto

import hu.attilakillin.coordinatoreventsbackend.dal.Event
import java.time.OffsetDateTime

/** A DTO class used for sending the summary of an event. */
data class EventSummaryDTO(
    /** The unique ID of the event, sent so that clients
     *  can later request the detailed event information using this identifier. */
    val id: Long,

    /** The moment an event was created. */
    val created: OffsetDateTime,

    /** The title of the event. */
    val title: String,
    /** The number of participants already registered to the event. */
    val participants: Int
)

/**
 * Convert an event to a summary DTO containing the number of participants to
 * the event, as well as the event title.
 */
fun Event.toSummaryDTO(): EventSummaryDTO {
    return EventSummaryDTO(
        id = this.id,
        created = this.created,
        title = this.title,
        participants = this.participants.size
    )
}
