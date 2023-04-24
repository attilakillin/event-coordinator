package hu.attilakillin.coordinatorarticlesbackend.dto

import hu.attilakillin.coordinatorarticlesbackend.dal.Article

/**
 * A DTO class used for sending and receiving a whole article.
 */
data class ArticleDTO(
    /** The title of the article. */
    val title: String,
    /** The main content of the article. Potentially unsafe HTML code
     *  if received, but always sanitized when sent by this service. */
    val content: String
)

/**
 * Convert an article to a DTO containing its title and its HTML content.
 */
fun Article.toDTO(): ArticleDTO {
    return ArticleDTO(
        title = this.title,
        content = this.content
    )
}
