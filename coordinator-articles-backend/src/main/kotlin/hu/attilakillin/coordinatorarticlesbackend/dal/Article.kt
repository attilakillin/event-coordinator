package hu.attilakillin.coordinatorarticlesbackend.dal

import jakarta.persistence.*
import java.time.OffsetDateTime

/**
 * The main domain specific entity of the application:
 * An article, with a title and an HTML encoded content.
 */
@Entity(name = "articles")
data class Article(
    /** Unique ID, used internally, not shown to the end user. */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /** The moment a draft article was created, or a published article published. */
    var created: OffsetDateTime,
    /** Whether the article is already published, or not. */
    var published: Boolean = false,

    /** The title of the article. */
    var title: String,
    /** The main content of the article. Sanitized HTML code. */
    var content: String,
    /** The text content of the article. This is what would appear if we
     *  stripped all HTML tags and formatting from the content field. */
    var text: String
)
