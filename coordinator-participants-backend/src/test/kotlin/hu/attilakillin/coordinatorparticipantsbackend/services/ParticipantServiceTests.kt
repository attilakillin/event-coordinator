package hu.attilakillin.coordinatorparticipantsbackend.services

import hu.attilakillin.coordinatorparticipantsbackend.dal.Participant
import hu.attilakillin.coordinatorparticipantsbackend.dal.ParticipantRepository
import hu.attilakillin.coordinatorparticipantsbackend.dto.ParticipantRequestDTO
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ParticipantServiceTests {
    private val repository = mockk<ParticipantRepository>()

    private val service = ParticipantService(repository)

    private val testParticipant = Participant(
        firstName = "First",
        lastName = "Last",
        email = "email",
        address = null,
        phoneNumber = null,
        notes = null
    )

    @Test
    fun `getAllParticipants() returns all participants`() {
        every { repository.findAll() } returns listOf(testParticipant)

        val result = service.getAllParticipants()

        Assertions.assertEquals(1, result.size)
    }

    @Test
    fun `getParticipantByEmail() returns null with nonexistent email`() {
        every { repository.findByEmail(any()) } returns null

        val result = service.getParticipantByEmail("email")

        Assertions.assertEquals(null, result)
    }

    @Test
    fun `getParticipantByEmail() returns participant with existing email`() {
        every { repository.findByEmail(any()) } returns testParticipant

        val result = service.getParticipantByEmail("email")

        Assertions.assertEquals(testParticipant, result)
    }

    @Test
    fun `saveParticipant() fails with empty first name`() {
        val dto = ParticipantRequestDTO(
            firstName = "",
            lastName = "Last",
            email = "email@ema.il",
            phoneNumber = null,
            address = null,
            notes = null
        )

        val result = service.saveParticipant(dto)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `saveParticipant() fails with empty last name`() {
        val dto = ParticipantRequestDTO(
            firstName = "First",
            lastName = "",
            email = "email@ema.il",
            phoneNumber = null,
            address = null,
            notes = null
        )

        val result = service.saveParticipant(dto)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `saveParticipant() fails with empty email`() {
        val dto = ParticipantRequestDTO(
            firstName = "First",
            lastName = "Last",
            email = "",
            phoneNumber = null,
            address = null,
            notes = null
        )

        val result = service.saveParticipant(dto)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `saveParticipant() fails with invalid email`() {
        val dto = ParticipantRequestDTO(
            firstName = "First",
            lastName = "Last",
            email = "invalid",
            phoneNumber = null,
            address = null,
            notes = null
        )

        val result = service.saveParticipant(dto)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `saveParticipant() saves with three main data points given`() {
        every { repository.save(any()) } returns testParticipant
        val dto = ParticipantRequestDTO(
            firstName = "First",
            lastName = "Last",
            email = "email@ema.il",
            phoneNumber = null,
            address = null,
            notes = null
        )

        val result = service.saveParticipant(dto)

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(testParticipant, result)
    }

    @Test
    fun `deleteParticipant() just runs`() {
        every { repository.deleteById(any()) } just runs

        Assertions.assertDoesNotThrow { service.deleteParticipant(0) }
    }
}