package hu.attilakillin.coordinatoreventsbackend.services

import hu.attilakillin.coordinatoreventsbackend.config.PropertiesConfiguration
import hu.attilakillin.coordinatoreventsbackend.dal.Event
import hu.attilakillin.coordinatoreventsbackend.dal.EventRepository
import hu.attilakillin.coordinatoreventsbackend.dto.EventRequestDTO
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.OffsetDateTime

/**
 * Performs tasks related to event persistence and manipulation.
 * Technically, this service class is the heart of the application.
 */
@Service
class EventService(
    private val repository: EventRepository,
    private val configuration: PropertiesConfiguration
) {

    /**
     * Checks the event for empty fields, and saves it if it has none.
     * Returns the persisted event, or null, if it failed the checks.
     * If the given event has an explicitly set ID, the method updates the saved entity.
     */
    private fun checkThenSaveEvent(event: Event): Event? {
        if (event.title.isBlank()) {
            return null
        }

        return repository.save(event)
    }

    /**
     * Saves event and returns the saved entity. If the event had empty
     * fields after sanitization, it isn't saved, and null is returned.
     */
    fun saveEvent(dto: EventRequestDTO): Event? {
        return checkThenSaveEvent(Event(
            created = OffsetDateTime.now(),
            title = dto.title,
            participants = mutableListOf()
        ))
    }

    /**
     * Updates event and returns the updated entity. If the given ID is invalid, or the
     * event had empty fields after sanitization, it isn't saved, and null is returned.
     */
    fun updateEvent(id: Long, dto: EventRequestDTO): Event? {
        val original = repository.findByIdOrNull(id)
            ?: return null

        val event = Event(
            id = original.id,
            created = original.created,
            title = dto.title,
            participants = original.participants
        )

        return checkThenSaveEvent(event)
    }

    /**
     * Deletes an event given by its ID. Does not care whether the ID
     * points to a valid entity, or not.
     */
    fun deleteEvent(id: Long) {
        repository.deleteById(id)
    }

    /**
     * Searches events by the given keywords as a search query.
     * If the keywords string is empty or null, returns every event.
     */
    fun searchEvents(keywords: String?): List<Event> {
        if (keywords.isNullOrBlank()) {
            return repository.findAll()
        }

        return repository.searchByTitle(keywords)
    }

    /**
     * Returns whether a specified email is registered to a specified event.
     */
    fun isRegisteredToEvent(email: String, id: Long): Boolean {
        return repository.findByIdOrNull(id)?.participants?.contains(email) ?: false
    }

    /**
     * Registers the given email to an event.
     */
    fun registerToEvent(email: String, id: Long): Boolean {
        if (isRegisteredToEvent(email, id)) return true

        val verification = RestTemplate().postForEntity(
            configuration.emailVerificationUrl,
            email,
            String::class.java
        )

        if (verification.statusCode.is2xxSuccessful) {
            val event = repository.findByIdOrNull(id) ?: return false
            event.participants.add(email)

            return true
        } else {
            return false
        }
    }

    /**
     * Returns a specific event by its ID. If the ID points to a
     * nonexistent entity, null is returned instead.
     */
    fun getEvent(id: Long): Event? {
        return repository.findByIdOrNull(id)
    }
}
