package hu.attilakillin.coordinatorarticlesbackend.controllers

import hu.attilakillin.coordinatorarticlesbackend.dto.*
import hu.attilakillin.coordinatorarticlesbackend.services.ArticleService
import hu.attilakillin.coordinatorarticlesbackend.services.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@CrossOrigin
class ArticleController(
    private val articleService: ArticleService,
    private val authService: AuthService
) {

    /**
     * Search articles by the specified keywords and return a list of summaries.
     * The keywords request parameter is optional. If it's not present, or empty,
     * every article is returned.
     *
     * Only works on published articles.
     */
    @GetMapping("/published")
    fun searchPublishedArticles(@RequestParam keywords: String?): ResponseEntity<List<ArticleSummaryDTO>> {
        val articles = articleService.searchArticles(keywords, published = true)

        return ResponseEntity.ok(articles.map { it.toSummaryDTO(200) })
    }

    /**
     * Search draft articles by the specified keywords and return a list of summaries.
     * The keywords request parameter is optional. If it's not present, or empty,
     * every draft article is returned.
     *
     * Only works on draft articles, and requires authentication. If the given authentication
     * token is invalid, returns an HTTP 403 response.
     */
    @GetMapping("/drafts")
    fun searchDraftArticles(
        @RequestHeader("Auth-Token") token: String?,
        @RequestParam keywords: String?
    ): ResponseEntity<List<ArticleSummaryDTO>> {
        if (!authService.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val articles = articleService.searchArticles(keywords, published = false)

        return ResponseEntity.ok(articles.map { it.toSummaryDTO(200) })
    }

    /**
     * Returns a specified article given by its ID.
     * Requires authentication for draft articles. If a draft article is requested,
     * and the given authentication token is invalid, returns an HTTP 403 response.
     * Otherwise, returns an HTTP 400 response if the ID is not a valid number,
     * and an HTTP 404 response if no article is present with this ID.
     *
     * For published articles, no authentication token is required.
     */
    @GetMapping("/administer/{id}")
    fun getArticle(
        @RequestHeader("Auth-Token") token: String?,
        @PathVariable id: String
    ): ResponseEntity<ArticleResponseDTO> {
        val validId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        val article = articleService.getArticle(validId)
            ?: return ResponseEntity.notFound().build()

        if (!article.published && !authService.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return ResponseEntity.ok(article.toDTO())
    }

    /**
     * Accepts an article in the form of a DTO, sanitizes and validates it,
     * and saves it in the database.
     * Authentication is required in the form of a JWT given in the 'Auth-Token' header.
     * Returns an HTTP 403 response if the given authentication token is invalid,
     * an HTTP 400 response if either the text, or the content field of the article
     * is empty after sanitization, or an HTTP 201 response if everything is valid.
     */
    @PostMapping("/administer")
    fun postArticle(
        @RequestHeader("Auth-Token") token: String?,
        @RequestBody dto: ArticleRequestDTO
    ): ResponseEntity<Unit> {
        if (!authService.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val article = articleService.saveArticle(dto)
            ?: return ResponseEntity.badRequest().build()

        /* Build the required URI for the '201 Created' response. */
        val uri = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(article.id)
            .toUri()

        return ResponseEntity.created(uri).build()
    }

    /**
     * Accepts an article in the form of a DTO, sanitizes and validates it,
     * and updates the article with the given ID with it.
     * Authentication is required in the form of a JWT given in the 'Auth-Token' header.
     * Returns an HTTP 403 response if the given authentication token is invalid,
     * an HTTP 400 response if the ID is not a valid number, or either the text
     * or the content field of the article is empty after sanitization,
     * or an empty HTTP 200 response if everything is valid.
     */
    @PutMapping("/administer/{id}")
    fun putArticle(
        @RequestHeader("Auth-Token") token: String?,
        @PathVariable id: String,
        @RequestBody dto: ArticleRequestDTO
    ): ResponseEntity<Unit> {
        if (!authService.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val validId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        articleService.updateArticle(validId, dto)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok().build()
    }

    /**
     * Deletes an article.
     * Authentication is required in the form of a JWT given in the 'Auth-Token' header.
     * Returns an HTTP 403 response if the given authentication token is invalid,
     * an HTTP 400 response if the ID is not a valid number, or an empty HTTP 204 response
     * otherwise, no matter if an article was deleted or not.
     */
    @DeleteMapping("/administer/{id}")
    fun deleteArticle(
        @RequestHeader("Auth-Token") token: String?,
        @PathVariable id: String
    ): ResponseEntity<Unit> {
        if (!authService.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val parsedId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        articleService.deleteArticle(parsedId)

        return ResponseEntity.noContent().build()
    }

    /**
     * Publishes an article.
     * Authentication is required in the form of a JWT given in the 'Auth-Token' header.
     * Returns an HTTP 403 response if the given authentication token is invalid,
     * an HTTP 400 response if the ID is not a valid number, or it doesn't link to a valid
     * draft article, or an empty HTTP 200 response if everything is valid.
     */
    @PostMapping("/administer/{id}/publish")
    fun publishArticle(
        @RequestHeader("Auth-Token") token: String?,
        @PathVariable id: String
    ): ResponseEntity<Unit> {
        if (!authService.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val validId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        articleService.publishArticle(validId)
            ?: return ResponseEntity.badRequest().build()

        return ResponseEntity.ok().build()
    }
}
