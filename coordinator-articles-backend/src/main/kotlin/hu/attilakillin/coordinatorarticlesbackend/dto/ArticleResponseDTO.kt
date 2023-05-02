package hu.attilakillin.coordinatorarticlesbackend.dto

import hu.attilakillin.coordinatorarticlesbackend.dal.Article
import java.time.OffsetDateTime

/**
 * A DTO class used for sending a whole article.
 */
data class ArticleResponseDTO(
    /** The moment a draft article was created, or a published article published. */
    val created: OffsetDateTime,
    /** Whether the article is already published, or not. */
    var published: Boolean,

    /** The title of the article. */
    val title: String,
    /** The main content of the article. Always sanitized when sent by this service. */
    val content: String
)

/**
 * Convert an article to a DTO containing its title and its HTML content.
 */
fun Article.toDTO(): ArticleResponseDTO {
    return ArticleResponseDTO(
        created = this.created,
        published = this.published,
        title = this.title,
        content = this.content
    )
}
