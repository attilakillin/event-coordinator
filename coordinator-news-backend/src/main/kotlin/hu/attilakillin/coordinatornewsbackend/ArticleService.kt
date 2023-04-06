package hu.attilakillin.coordinatornewsbackend

import hu.attilakillin.coordinatornewsbackend.dal.Article
import hu.attilakillin.coordinatornewsbackend.dal.ArticleRepository
import hu.attilakillin.coordinatornewsbackend.dto.ArticleDTO
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val repository: ArticleRepository
) {

    fun saveArticle(article: ArticleDTO): Article {
        val sanitizedContent = Jsoup.clean(article.content, Safelist.basicWithImages().addAttributes("span", "style"))
        val textContent = Jsoup.parse(sanitizedContent).wholeText()

        return repository.save(Article(
            title =  article.title,
            text = textContent,
            content = sanitizedContent
        ))
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
