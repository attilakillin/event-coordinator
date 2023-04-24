package hu.attilakillin.coordinatorarticlesbackend.dto

import hu.attilakillin.coordinatorarticlesbackend.dal.Article

/** A DTO class used for sending the summary of an article. */
data class ArticleSummaryDTO(
    /** The unique ID of the article, sent so that clients
     *  can later request the whole article using this identifier. */
    val id: Long,
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
        title = this.title,
        summary = this.text.take(chars)
    )
}
