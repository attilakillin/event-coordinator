package hu.attilakillin.coordinatoreventsbackend.services

import hu.attilakillin.coordinatoreventsbackend.dal.EventRepository
import hu.attilakillin.coordinatoreventsbackend.dto.CheckinDTO
import hu.attilakillin.coordinatoreventsbackend.dto.getValidStatus
import hu.attilakillin.coordinatoreventsbackend.dto.toCheckinDTOList
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 * Performs tasks related to participant check-in management.
 */
@Service
class CheckinService(
    private val repository: EventRepository
) {

    /**
     * Retrieve the list of all participant emails and check-in statuses.
     * Returns null if the event ID is invalid.
     */
    fun getCheckinsForEvent(eventId: Long): List<CheckinDTO>? {
        val event = repository.findByIdOrNull(eventId) ?: return null

        return event.toCheckinDTOList()
    }

    /**
     * Updates one check-in status for an event. Returns true if it succeeded,
     * or false if either of the event ID or the participant email was invalid.
     */
    fun updateCheckinForEvent(dto: CheckinDTO): Boolean {
        val event = repository.findByIdOrNull(dto.eventId) ?: return false
        if (!event.participants.contains(dto.email)) return false
        val status = dto.getValidStatus() ?: return false

        event.participants.set(dto.email, status)

        repository.save(event)
        return true
    }
}
