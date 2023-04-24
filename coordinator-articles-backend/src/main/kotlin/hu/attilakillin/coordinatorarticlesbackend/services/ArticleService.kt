package hu.attilakillin.coordinatorarticlesbackend.services

import hu.attilakillin.coordinatorarticlesbackend.dal.Article
import hu.attilakillin.coordinatorarticlesbackend.dal.ArticleRepository
import hu.attilakillin.coordinatorarticlesbackend.dto.ArticleDTO
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

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
    private fun sanitizeDTO(dto: ArticleDTO): Article {
        /* Custom sanitizer that allows additional tags created by the frontend editor. */
        val sanitizer = Safelist.basicWithImages()
            .addAttributes("span", "style", "class")
            .addAttributes("h1", "class")
            .addAttributes("h2", "class")

        /* Clean content field and return with a new object. */
        val cleanContent = Jsoup.clean(dto.content, sanitizer)
        val cleanText = Jsoup.parse(cleanContent).wholeText()

        return Article(
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
    fun saveArticle(dto: ArticleDTO): Article? {
        val article = sanitizeDTO(dto)

        return checkThenSaveArticle(article)
    }

    /**
     * Updates article and returns the updated entity. If the given ID is invalid, or the
     * article had empty fields after sanitization, it isn't saved, and null is returned.
     */
    fun updateArticle(id: Long, dto: ArticleDTO): Article? {
        if(!repository.existsById(id)) {
            return null
        }

        val article = sanitizeDTO(dto).also { it.id = id }

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
     * Searches the articles by the given keywords as a search query.
     * If the keywords string is empty or null, returns every article.
     */
    fun searchArticles(keywords: String?): List<Article> {
        if (keywords.isNullOrBlank()) {
            return repository.findAll()
        }

        return repository.searchArticles(keywords, Pageable.unpaged()).content
    }

    /**
     * Returns a specific article by its ID. If the ID points to a
     * nonexistent entity, null is returned instead.
     */
    fun getArticle(id: Long): Article? {
        return repository.findByIdOrNull(id)
    }
}
