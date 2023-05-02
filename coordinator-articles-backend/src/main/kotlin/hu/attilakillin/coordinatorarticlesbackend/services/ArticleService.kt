package hu.attilakillin.coordinatorarticlesbackend.services

import hu.attilakillin.coordinatorarticlesbackend.dal.Article
import hu.attilakillin.coordinatorarticlesbackend.dal.ArticleRepository
import hu.attilakillin.coordinatorarticlesbackend.dto.ArticleRequestDTO
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

/**
 * Performs tasks related to article persistence and manipulation.
 * Technically, this service class is the heart of the application.
 */
@Service
class ArticleService(
    private val repository: ArticleRepository
) {
    /**
     * HTML sanitization function. Sanitizes the HTML content from an
     * article and creates an article object with the resulting clean content.
     */
    private fun sanitizeDTO(dto: ArticleRequestDTO): Article {
        /* Custom sanitizer that allows additional tags created by the frontend editor. */
        val sanitizer = Safelist.basic()
            .addAttributes("span", "style", "class")
            .addAttributes("h1", "class")
            .addAttributes("h2", "class")

        /* Clean content field and return with a new object. */
        val cleanContent = Jsoup.clean(dto.content, sanitizer)
        val cleanText = Jsoup.parse(cleanContent).wholeText()

        val created = OffsetDateTime.now()

        return Article(
            created = created,
            published = false,

            title = dto.title,
            content = cleanContent,
            text = cleanText
        )
    }

    /**
     * Checks the article for empty fields, and saves it if it has none.
     * Returns the persisted article, or null, if it failed the checks.
     * If the given article has an explicitly set ID, the method updates the saved entity.
     */
    private fun checkThenSaveArticle(article: Article): Article? {
        if (article.title.isBlank() || article.text.isBlank()) {
            return null
        }

        return repository.save(article)
    }

    /**
     * Saves article and returns the saved entity. If the article had empty
     * fields after sanitization, it isn't saved, and null is returned.
     */
    fun saveArticle(dto: ArticleRequestDTO): Article? {
        val article = sanitizeDTO(dto)

        return checkThenSaveArticle(article)
    }

    /**
     * Updates article and returns the updated entity. If the given ID is invalid, or the
     * article had empty fields after sanitization, it isn't saved, and null is returned.
     */
    fun updateArticle(id: Long, dto: ArticleRequestDTO): Article? {
        val original = repository.findByIdOrNull(id)
            ?: return null

        val article = sanitizeDTO(dto).also {
            it.id = original.id
            it.created = original.created
            it.published = original.published
        }

        return checkThenSaveArticle(article)
    }

    /**
     * Deletes an article given by its ID. Does not care whether the ID
     * points to a valid entity, or not.
     */
    fun deleteArticle(id: Long) {
        repository.deleteById(id)
    }

    /**
     * Publishes an already saved article. If the given ID is invalid, null is returned.
     */
    fun publishArticle(id: Long): Article? {
        val article = repository.findByIdOrNull(id) ?: return null

        article.published = true
        article.created = OffsetDateTime.now()

        return repository.save(article)
    }

    /**
     * Searches articles by the given keywords as a search query.
     * Searches the list of published, or draft articles, based on the published parameter.
     * If the keywords string is empty or null, returns every published/draft article.
     */
    fun searchArticles(keywords: String?, published: Boolean): List<Article> {
        if (keywords.isNullOrBlank()) {
            return repository.findAllArticlesByPublication(published)
        }

        return repository.searchArticlesByPublication(keywords, published, Pageable.unpaged()).content
    }

    /**
     * Returns a specific article by its ID. If the ID points to a
     * nonexistent entity, null is returned instead.
     */
    fun getArticle(id: Long): Article? {
        return repository.findByIdOrNull(id)
    }
}
