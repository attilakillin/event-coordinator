package hu.attilakillin.coordinatoreventsbackend.dal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * JPA repository. The database table is initialized from the 'schema.sql' resource file.
 */
@Repository
interface EventRepository : JpaRepository<Event, Long> {

    @Query(value = """
        SELECT * FROM events
        WHERE title LIKE CONCAT('%', :keywords, '%')
        ORDER BY
            title LIKE CONCAT(:keywords, '%') desc,
            IFNULL(NULLIF(INSTR(title, CONCAT(' ', :keywords)), 0), 99999),
            IFNULL(NULLIF(INSTR(title, :keywords), 0), 99999),
            
            created DESC;
    """, nativeQuery = true)
    fun searchByTitle(keywords: String): List<Event>
}
