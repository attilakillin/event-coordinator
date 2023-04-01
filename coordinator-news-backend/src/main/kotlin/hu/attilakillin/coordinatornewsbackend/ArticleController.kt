package hu.attilakillin.coordinatornewsbackend

import hu.attilakillin.coordinatornewsbackend.dto.ArticleDTO
import hu.attilakillin.coordinatornewsbackend.dto.ArticleSummaryDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
class ArticleController(
    private val service: ArticleService
) {

    @GetMapping("/")
    fun searchArticles(@RequestParam keywords: String?): ResponseEntity<List<ArticleSummaryDTO>> {
        val articles = if (keywords.isNullOrBlank()) {
            service.getAllArticles()
        } else {
            service.searchArticles(keywords)
        }

        return ResponseEntity.ok(articles.map {
            return@map ArticleSummaryDTO(it.id, it.title, it.text.take(200))
        })
    }

    @GetMapping("/{id}")
    fun getArticle(@PathVariable id: String): ResponseEntity<ArticleDTO> {
        val parsedId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()
        val article = service.getArticleOrNull(parsedId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(ArticleDTO(article.title, article.content))
    }

    @PostMapping("/")
    fun postArticle(@RequestBody article: ArticleDTO): ResponseEntity<Unit> {
        val savedArticle = service.saveArticle(article)

        val uri = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(savedArticle.id)
            .toUri()

        return ResponseEntity.created(uri).build()
    }

    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: String): ResponseEntity<Unit> {
        val parsedId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        service.deleteArticle(parsedId)

        return ResponseEntity.noContent().build()
    }
}
