package hu.attilakillin.coordinatoreventsbackend.services

import hu.attilakillin.coordinatoreventsbackend.dal.CheckinStatus
import hu.attilakillin.coordinatoreventsbackend.dal.Event
import hu.attilakillin.coordinatoreventsbackend.dal.EventRepository
import hu.attilakillin.coordinatoreventsbackend.dto.CheckinDTO
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime

@ExtendWith(MockKExtension::class)
class CheckinServiceTests {
    private val repository = mockk<EventRepository>()

    private val service = CheckinService(repository)

    private val testEvent = Event( created = OffsetDateTime.now(), title = "Content", participants = mutableMapOf() )
    private val testCheckin = CheckinDTO( eventId = 0, email = "email", status = "UNKNOWN" )

    @Test
    fun `getCheckinsForEvent() returns null for nonexistent event`() {
        every { repository.findByIdOrNull(any()) } returns null

        val result = service.getCheckinsForEvent(0)

        Assertions.assertEquals(null, result)
    }

    @Test
    fun `getCheckinsForEvent() returns checkins for existing event`() {
        every { repository.findByIdOrNull(any()) } returns
                testEvent.copy(participants = mutableMapOf("email" to CheckinStatus.UNKNOWN))

        val result = service.getCheckinsForEvent(0)

        Assertions.assertNotEquals(null, result)
        Assertions.assertEquals("email", result?.first()?.email)
        Assertions.assertEquals("UNKNOWN", result?.first()?.status)
    }

    @Test
    fun `updateCheckinForEvent() returns false with nonexistent event`() {
        every { repository.findByIdOrNull(any()) } returns null

        val result = service.updateCheckinForEvent(testCheckin)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(false, result)
    }

    @Test
    fun `updateCheckinForEvent() returns false with non-registered participant`() {
        every { repository.findByIdOrNull(any()) } returns testEvent

        val result = service.updateCheckinForEvent(testCheckin)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(false, result)
    }

    @Test
    fun `updateCheckinForEvent() returns false with non-valid DTO status`() {
        every { repository.findByIdOrNull(any()) } returns
                testEvent.copy(participants = mutableMapOf("email" to CheckinStatus.UNKNOWN))

        val result = service.updateCheckinForEvent(testCheckin.copy(status = "INVALID"))

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(false, result)
    }

    @Test
    fun `updateCheckinForEvent() returns true when everything is valid`() {
        every { repository.findByIdOrNull(any()) } returns
                testEvent.copy(participants = mutableMapOf("email" to CheckinStatus.UNKNOWN))
        every { repository.save(any()) } returns testEvent

        val result = service.updateCheckinForEvent(testCheckin.copy(status = "CHECKED_IN"))

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(true, result)
    }
}
