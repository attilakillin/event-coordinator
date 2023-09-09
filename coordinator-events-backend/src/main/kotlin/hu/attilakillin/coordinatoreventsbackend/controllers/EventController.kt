package hu.attilakillin.coordinatoreventsbackend.controllers

import hu.attilakillin.coordinatoreventsbackend.dto.*
import hu.attilakillin.coordinatoreventsbackend.services.EventService
import hu.attilakillin.coordinatoreventsbackend.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
class EventController(
    private val eventService: EventService,
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

    /**
     * Search events by the specified keywords and return a list of summaries.
     * The keywords request parameter is optional. If it's not present, or empty,
     * every event is returned.
     */
    @GetMapping("/")
    fun searchEvents(@RequestParam keywords: String?): ResponseEntity<List<EventSummaryDTO>> {
        val events = eventService.searchEvents(keywords,)

        return ResponseEntity.ok(events.map { it.toSummaryDTO() })
    }

    /**
     * Returns a specified event given by its ID.
     * Requires authentication. If the given authentication token is invalid,
     * returns an HTTP 403 response.
     * Otherwise, returns an HTTP 400 response if the ID is not a valid number,
     * and an HTTP 404 response if no event is present with this ID.
     */
    @GetMapping("/administer/{id}")
    fun getEvent(
        @PathVariable id: String,
        @RequestHeader("Auth-Token") token: String?,
        req: HttpServletRequest
    ): ResponseEntity<EventResponseDTO> {
        if (!isTokenValid(token, req)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val validId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        val event = eventService.getEvent(validId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(event.toDTO())
    }

    /**
     * Accepts an event in the form of a DTO, validates it, and saves it in the database.
     * Authentication is required in the form of a JWT given in the 'Auth-Token' header.
     * Returns an HTTP 403 response if the given authentication token is invalid,
     * an HTTP 400 response if the title is empty, or an HTTP 201 response if everything is valid.
     */
    @PostMapping("/administer")
    fun postEvent(
        @RequestBody dto: EventRequestDTO,
        @RequestHeader("Auth-Token") token: String?,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        val (valid, subject) = isTokenValidWithSubject(token, req)
        if (!valid || subject == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val event = eventService.saveEvent(dto)
            ?: return ResponseEntity.badRequest().build()

        val uri = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(event.id)
            .toUri()

        logger.logEventModified(req, event.id, "Created", subject)
        return ResponseEntity.created(uri).build()
    }

    /**
     * Accepts an event in the form of a DTO, validates it,
     * and updates the event with the given ID with it.
     * Authentication is required in the form of a JWT given in the 'Auth-Token' header.
     * Returns an HTTP 403 response if the given authentication token is invalid,
     * an HTTP 400 response if the title is empty, or an HTTP 200 response if everything is valid.
     */
    @PutMapping("/administer/{id}")
    fun putEvent(
        @RequestHeader("Auth-Token") token: String?,
        @PathVariable id: String,
        @RequestBody dto: EventRequestDTO,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        val (valid, subject) = isTokenValidWithSubject(token, req)
        if (!valid || subject == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val validId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        eventService.updateEvent(validId, dto)
            ?: return ResponseEntity.notFound().build()

        logger.logEventModified(req, validId, "Updated", subject)
        return ResponseEntity.ok().build()
    }

    /**
     * Deletes an event.
     * Authentication is required in the form of a JWT given in the 'Auth-Token' header.
     * Returns an HTTP 403 response if the given authentication token is invalid,
     * an HTTP 400 response if the ID is not a valid number, or an empty HTTP 204 response
     * otherwise, no matter if an event was deleted or not.
     */
    @DeleteMapping("/administer/{id}")
    fun deleteEvent(
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

        eventService.deleteEvent(parsedId)

        logger.logEventModified(req, parsedId, "Deleted", subject)
        return ResponseEntity.noContent().build()
    }

    /**
     * Registers a given email to a given event.
     *
     * Returns an HTTP 400 response if the ID is not a valid number, or
     * does not point to a valid event. Returns an HTTP 403 response
     * if the registration was unsuccessful
     * (probably because the email wasn't registered on the service we use
     * to validate first).
     * If everything succeeded, returns an HTTP 200 response.
     */
    @PostMapping("/register/{id}")
    fun registerToEvent(
        @PathVariable id: String,
        @RequestBody email: String,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        val parsedId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        eventService.getEvent(parsedId)
            ?: return ResponseEntity.badRequest().build()

        val success = eventService.registerToEvent(email, parsedId)
        if (!success) {
            logger.logRegistrationUnsuccessful(req, parsedId, email)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        return ResponseEntity.ok().build()
    }
}

/* Logging extension functions. */

private val HttpServletRequest.realIp get() = getHeader("X-Real-Ip")

private fun Logger.logTokenNotVerified(req: HttpServletRequest) {
    val message = "Token verification failed: (IP: '{}', requested path: '{}', method: '{}')"
    info(message, req.realIp, req.requestURI, req.method)
}

private fun Logger.logEventModified(req: HttpServletRequest, event: Long, action: String, subject: String) {
    val message = "{} event: (id: '{}', subject: '{}', IP '{}')"
    info(message, action, event, subject, req.realIp)
}

private fun Logger.logRegistrationUnsuccessful(req: HttpServletRequest, event: Long, email: String) {
    val message = "Event registration unsuccessful: (event: '{}', email: '{}', IP '{}')"
    info(message, event, email, req.realIp)
}
