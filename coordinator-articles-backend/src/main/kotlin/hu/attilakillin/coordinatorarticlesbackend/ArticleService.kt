package hu.attilakillin.coordinatorarticlesbackend

import hu.attilakillin.coordinatorarticlesbackend.dal.Article
import hu.attilakillin.coordinatorarticlesbackend.dal.ArticleRepository
import hu.attilakillin.coordinatorarticlesbackend.dto.ArticleDTO
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val repository: ArticleRepository
) {
    private val sanitizer = Safelist.basicWithImages().addAttributes("span", "style", "class")

    fun saveArticle(article: ArticleDTO): Article {
        val sanitizedContent = Jsoup.clean(article.content, sanitizer)
        val textContent = Jsoup.parse(sanitizedContent).wholeText()

        return repository.save(Article(
            title =  article.title,
            text = textContent,
            content = sanitizedContent
        ))
    }

    fun updateArticle(id: Long, dto: ArticleDTO): Article? {
        val sanitizedContent = Jsoup.clean(dto.content, sanitizer)
        val textContent = Jsoup.parse(sanitizedContent).wholeText()

        val article = repository.findByIdOrNull(id) ?: return null

        article.title = dto.title
        article.text = textContent
        article.content = sanitizedContent

        return repository.save(article)
    }

    fun deleteArticle(id: Long) {
        repository.deleteById(id)
    }

    fun getArticleOrNull(id: Long): Article? {
        return repository.findByIdOrNull(id)
    }

    fun searchArticles(keywords: String): List<Article> {
        // TODO At the moment, pagination is explicitly disabled
        return repository.searchArticles(keywords, Pageable.unpaged()).content
    }

    fun getAllArticles(): List<Article> {
        return repository.findAll()
    }
}
