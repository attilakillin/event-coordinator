package hu.attilakillin.coordinatorparticipantsbackend.dal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository. The database table is initialized from the 'schema.sql' resource file.
 */
@Repository
interface ParticipantRepository : JpaRepository<Participant, Long> {
}
