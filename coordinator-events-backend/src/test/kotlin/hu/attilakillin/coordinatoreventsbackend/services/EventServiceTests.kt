package hu.attilakillin.coordinatoreventsbackend.services

import hu.attilakillin.coordinatoreventsbackend.config.PropertiesConfiguration
import hu.attilakillin.coordinatoreventsbackend.dal.CheckinStatus
import hu.attilakillin.coordinatoreventsbackend.dal.Event
import hu.attilakillin.coordinatoreventsbackend.dal.EventRepository
import hu.attilakillin.coordinatoreventsbackend.dto.EventRequestDTO
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime

@ExtendWith(MockKExtension::class)
class EventServiceTests {
    private val configuration = mockk<PropertiesConfiguration>()
    private val repository = mockk<EventRepository>()

    private val service = EventService(repository, configuration)

    private val testEvent = Event( created = OffsetDateTime.now(), title = "Content", participants = mutableMapOf() )

    @Test
    fun `saveEvent() doesn't allow empty titles`() {
        val result = service.saveEvent(EventRequestDTO( title = "" ))

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `saveEvent() saves with non-empty title`() {
        every { repository.save(any()) } returns testEvent

        val result = service.saveEvent(EventRequestDTO( title = "Content" ))

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(testEvent, result)
    }

    @Test
    fun `updateEvent() doesn't allow nonexistent events`() {
        every { repository.findByIdOrNull(any()) } returns null

        val result = service.updateEvent(0, EventRequestDTO( title = "Content" ))

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `updateEvent() doesn't allow empty titles`() {
        every { repository.findByIdOrNull(any()) } returns testEvent

        val result = service.updateEvent(0, EventRequestDTO( title = "" ))

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `updateEvent() saves with existing ID and non-empty title`() {
        every { repository.findByIdOrNull(any()) } returns testEvent
        every { repository.save(any()) } returns testEvent

        val result = service.updateEvent(0, EventRequestDTO( title = "Content" ))

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(testEvent, result)
    }

    @Test
    fun `deleteEvent() just runs`() {
        every { repository.deleteById(any()) } just runs

        Assertions.assertDoesNotThrow { service.deleteEvent(0) }
    }

    @Test
    fun `searchEvents() returns all without query`() {
        every { repository.findAllOrdered() } returns listOf(testEvent, testEvent)
        every { repository.searchByTitle(any()) } returns listOf(testEvent)

        val result = service.searchEvents("")

        verify(exactly = 1) { repository.findAllOrdered() }
        verify(exactly = 0) { repository.searchByTitle(any()) }
        Assertions.assertEquals(listOf(testEvent, testEvent), result)
    }

    @Test
    fun `searchEvents() filters with query`() {
        every { repository.findAllOrdered() } returns listOf(testEvent, testEvent)
        every { repository.searchByTitle(any()) } returns listOf(testEvent)

        val result = service.searchEvents("query")

        verify(exactly = 0) { repository.findAllOrdered() }
        verify(exactly = 1) { repository.searchByTitle("query") }
        Assertions.assertEquals(listOf(testEvent), result)
    }

    @Test
    fun `isRegisteredToEvent() returns false with nonexistent event`() {
        every { repository.findByIdOrNull(any()) } returns null

        val result = service.isRegisteredToEvent("email", 0)

        Assertions.assertEquals(false, result)
    }

    @Test
    fun `isRegisteredToEvent() returns false with non-registered email`() {
        every { repository.findByIdOrNull(any()) } returns testEvent

        val result = service.isRegisteredToEvent("email", 0)

        Assertions.assertEquals(false, result)
    }

    @Test
    fun `isRegisteredToEvent() returns true with registered email`() {
        every { repository.findByIdOrNull(any()) } returns
                testEvent.copy(participants = mutableMapOf("email" to CheckinStatus.UNKNOWN))

        val result = service.isRegisteredToEvent("email", 0)

        Assertions.assertEquals(true, result)
    }

    @Test
    fun `registerToEvent()`() {
        // A locally-constructed RestTemplate currently prevents testing this.
    }

    @Test
    fun `unregisterFromEvent() returns false with nonexistent event`() {
        every { repository.findByIdOrNull(any()) } returns null

        val result = service.unregisterFromEvent("email", 0)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(false, result)
    }

    @Test
    fun `unregisterFromEvent() returns true with non-registered participant`() {
        every { repository.findByIdOrNull(any()) } returns testEvent

        val result = service.unregisterFromEvent("email", 0)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(true, result)
    }

    @Test
    fun `unregisterFromEvent() returns true with registered participant`() {
        every { repository.findByIdOrNull(any()) } returns
                testEvent.copy(participants = mutableMapOf("email" to CheckinStatus.UNKNOWN))
        every { repository.save(any()) } returns testEvent

        val result = service.unregisterFromEvent("email", 0)

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(true, result)
    }

    @Test
    fun `getEvent() returns null with nonexistent ID`() {
        every { repository.findByIdOrNull(any()) } returns null

        val result = service.getEvent(0)

        Assertions.assertEquals(null, result)
    }

    @Test
    fun `getEvent() returns event with valid ID`() {
        every { repository.findByIdOrNull(any()) } returns testEvent

        val result = service.getEvent(0)

        Assertions.assertEquals(testEvent, result)
    }
}
