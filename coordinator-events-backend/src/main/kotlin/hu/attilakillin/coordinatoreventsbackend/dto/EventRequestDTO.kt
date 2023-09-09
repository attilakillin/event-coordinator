package hu.attilakillin.coordinatoreventsbackend.dto

/**
 * A DTO class used for receiving an event creation request.
 */
data class EventRequestDTO(
    /** The title of the event. */
    val title: String
)
