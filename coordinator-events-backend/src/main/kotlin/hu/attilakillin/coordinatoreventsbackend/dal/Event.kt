package hu.attilakillin.coordinatoreventsbackend.dal

import jakarta.persistence.*
import java.time.OffsetDateTime

/**
 * The main domain specific entity of the application:
 * An event, with a title, and a list of emails registered to it.
 */
@Entity(name = "events")
data class Event(
    /** Unique ID, used internally, not shown to the end user. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /** The moment an event was created. */
    var created: OffsetDateTime,

    /** The title of the event. */
    var title: String,

    /** The list of registered participant emails to this event */
    @ElementCollection
    @CollectionTable(name = "participants", joinColumns = [JoinColumn(name = "id")])
    @Column(name = "email")
    var participants: MutableList<String>
)
