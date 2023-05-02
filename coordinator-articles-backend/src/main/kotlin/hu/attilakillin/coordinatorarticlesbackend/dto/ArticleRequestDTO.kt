package hu.attilakillin.coordinatorarticlesbackend.dto

/**
 * A DTO class used for receiving a whole article.
 */
data class ArticleRequestDTO(
    /** The title of the article. */
    val title: String,
    /** The main content of the article. Potentially unsafe HTML code. */
    val content: String
)
