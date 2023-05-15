package hu.attilakillin.coordinatorparticipantsbackend.controllers

import hu.attilakillin.coordinatorparticipantsbackend.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class ParticipantController(
    private val authService: AuthService
) {
    /**
     * Logging instance. All logging is done by the controller in
     * order to get as much additional information as possible.
     */
    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Authenticate with the given token, and return whether the token is valid, or not.
     */
    fun isTokenValid(token: String?, req: HttpServletRequest): Boolean {
        val claims = authService.getVerifiedClaims(token)
        if (claims == null) {
            logger.logTokenNotVerified(req)
            return false
        }

        return authService.validateClaims(claims)
    }

    /**
     * Authenticate with the given token, and return whether the token is valid or not,
     * as well as the subject administrator's name, if it is valid.
     */
    fun isTokenValidWithSubject(token: String?, req: HttpServletRequest): Pair<Boolean, String?> {
        val claims = authService.getVerifiedClaims(token)
        if (claims == null) {
            logger.logTokenNotVerified(req)
            return Pair(false, null)
        }

        val valid = authService.validateClaims(claims)
        return Pair(valid, claims.subject)
    }

    /*
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
        @RequestParam keywords: String?,
        @RequestHeader("Auth-Token") token: String?,
        req: HttpServletRequest
    ): ResponseEntity<List<ArticleSummaryDTO>> {
        if (!isTokenValid(token, req)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

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
        @PathVariable id: String,
        @RequestHeader("Auth-Token") token: String?,
        req: HttpServletRequest
    ): ResponseEntity<ArticleResponseDTO> {
        val validId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        val article = articleService.getArticle(validId)
            ?: return ResponseEntity.notFound().build()

        if (!article.published && !isTokenValid(token, req)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

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
        @RequestBody dto: ArticleRequestDTO,
        @RequestHeader("Auth-Token") token: String?,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        val (valid, subject) = isTokenValidWithSubject(token, req)
        if (!valid || subject == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val article = articleService.saveArticle(dto)
            ?: return ResponseEntity.badRequest().build()

        val uri = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(article.id)
            .toUri()

        logger.logArticleModified(req, article.id, "Created", subject)
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
        @RequestBody dto: ArticleRequestDTO,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        val (valid, subject) = isTokenValidWithSubject(token, req)
        if (!valid || subject == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val validId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        articleService.updateArticle(validId, dto)
            ?: return ResponseEntity.notFound().build()

        logger.logArticleModified(req, validId, "Updated", subject)
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
        @PathVariable id: String,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        val (valid, subject) = isTokenValidWithSubject(token, req)
        if (!valid || subject == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val parsedId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        articleService.deleteArticle(parsedId)

        logger.logArticleModified(req, parsedId, "Deleted", subject)
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
        @PathVariable id: String,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        val (valid, subject) = isTokenValidWithSubject(token, req)
        if (!valid || subject == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val validId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        articleService.publishArticle(validId)
            ?: return ResponseEntity.badRequest().build()

        logger.logArticleModified(req, validId, "Published", subject)
        return ResponseEntity.ok().build()
    }
    */
}

/* Logging extension functions. */

private val HttpServletRequest.realIp get() = getHeader("X-Real-Ip")

private fun Logger.logTokenNotVerified(req: HttpServletRequest) {
    val message = "Token verification failed: (IP: '{}', requested path: '{}', method: '{}')"
    info(message, req.realIp, req.requestURI, req.method)
}

/*
private fun Logger.logArticleModified(req: HttpServletRequest, article: Long, action: String, subject: String) {
    val message = "{} article: (id: '{}', subject: '{}', IP '{}')"
    info(message, action, article, subject, req.realIp)
}*/
