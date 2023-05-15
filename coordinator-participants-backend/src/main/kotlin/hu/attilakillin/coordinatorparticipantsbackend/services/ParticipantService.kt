package hu.attilakillin.coordinatorparticipantsbackend.services

import hu.attilakillin.coordinatorparticipantsbackend.dal.Participant
import hu.attilakillin.coordinatorparticipantsbackend.dal.ParticipantRepository
import hu.attilakillin.coordinatorparticipantsbackend.dto.ParticipantRequestDTO
import org.springframework.stereotype.Service

/**
 * Performs tasks related to participant persistence and manipulation.
 * Technically, this service class is the heart of the application.
 */
@Service
class ParticipantService(
    private val repository: ParticipantRepository
) {
    /**
     * Sanitization function. Sanitizes the incoming content from a participant
     * registration and creates an entity object with the resulting valid content.
     */
    private fun sanitizeDTO(dto: ParticipantRequestDTO): Participant? {
        if (dto.lastName.isBlank() || dto.firstName.isBlank()) {
            return null
        }

        if (!dto.email.matches(Regex("""^.+@.+\..+"""))) {
            return null
        }

        return Participant(
            lastName = dto.lastName,
            firstName = dto.firstName,
            email = dto.email,
            address = dto.address,
            phoneNumber = dto.phoneNumber,
            notes = dto.notes
        )
    }

    /**
     * Returns the list of all participants.
     */
    fun getAllParticipants(): List<Participant> {
        return repository.findAll()
    }

    /**
     * Saves a participant.
     * Returns null if validation failed, else returns the saved entity.
     */
    fun saveParticipant(dto: ParticipantRequestDTO): Participant? {
        val participant = sanitizeDTO(dto) ?: return null
        return repository.save(participant)
    }

    /**
     * Deletes a participants. Does not care whether the ID points to a valid
     * participant or not.
     */
    fun deleteParticipant(id: Long) {
        repository.deleteById(id)
    }
}
