package hu.attilakillin.coordinatorarticlesbackend.dto

import hu.attilakillin.coordinatorarticlesbackend.dal.Article
import java.time.OffsetDateTime

/** A DTO class used for sending the summary of an article. */
data class ArticleSummaryDTO(
    /** The unique ID of the article, sent so that clients
     *  can later request the whole article using this identifier. */
    val id: Long,

    /** The moment a draft article was created, or a published article published. */
    val created: OffsetDateTime,
    /** Whether the article is already published, or not. */
    val published: Boolean,

    /** The title of the article. */
    val title: String,
    /** A plain-text summary of the article. */
    val summary: String
)

/**
 * Convert an article to a summary DTO containing the first n characters
 * of the article's text content as its summary.
 */
fun Article.toSummaryDTO(chars: Int): ArticleSummaryDTO {
    return ArticleSummaryDTO(
        id = this.id,
        published = this.published,
        created = this.created,
        title = this.title,
        summary = this.text.take(chars)
    )
}
