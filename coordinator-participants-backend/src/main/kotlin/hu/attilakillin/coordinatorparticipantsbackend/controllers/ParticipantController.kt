package hu.attilakillin.coordinatorparticipantsbackend.controllers

import hu.attilakillin.coordinatorparticipantsbackend.dal.Participant
import hu.attilakillin.coordinatorparticipantsbackend.dto.ParticipantRequestDTO
import hu.attilakillin.coordinatorparticipantsbackend.dto.ParticipantResponseDTO
import hu.attilakillin.coordinatorparticipantsbackend.dto.toDTO
import hu.attilakillin.coordinatorparticipantsbackend.services.AuthService
import hu.attilakillin.coordinatorparticipantsbackend.services.ParticipantService
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class ParticipantController(
    private val participantService: ParticipantService,
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
     * Returns all participants.
     *
     * Requires authentication. If the given authentication
     * token is invalid, returns an HTTP 403 response.
     */
    @GetMapping("/")
    fun getAllParticipants(
        @RequestHeader("Auth-Token") token: String?,
        req: HttpServletRequest
    ): ResponseEntity<List<ParticipantResponseDTO>> {
        if (!isTokenValid(token, req)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val participants = participantService.getAllParticipants()
        return ResponseEntity.ok(participants.map { it.toDTO() })
    }

    /**
     * Accepts a participant registration request in the form of a DTO, validates it,
     * and saves it in the database.
     *
     * Authentication is required in the form of a JWT given in the 'Auth-Token' header.
     * Returns an HTTP 400 response if any of the input fields failed validation,
     * or an HTTP 200 response if everything is valid.
     */
    @PostMapping("/")
    fun postParticipant(
        @RequestBody dto: ParticipantRequestDTO,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        val participant = participantService.saveParticipant(dto)
            ?: return ResponseEntity.badRequest().build()

        logger.logParticipantRegistered(participant, req)
        return ResponseEntity.ok().build()
    }

    /**
     * Deletes a participant registration.
     *
     * Authentication is required in the form of a JWT given in the 'Auth-Token' header.
     * Returns an HTTP 403 response if the given authentication token is invalid,
     * an HTTP 400 response if the ID is not a valid number, or an empty HTTP 204 response
     * otherwise, no matter if a participant was deleted or not.
     */
    @DeleteMapping("/{id}")
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

        participantService.deleteParticipant(parsedId)

        logger.logParticipantModified(req, parsedId, "Deleted", subject)
        return ResponseEntity.noContent().build()
    }
}

/* Logging extension functions. */

private val HttpServletRequest.realIp get() = getHeader("X-Real-Ip")

private fun Logger.logTokenNotVerified(req: HttpServletRequest) {
    val message = "Token verification failed: (IP: '{}', requested path: '{}', method: '{}')"
    info(message, req.realIp, req.requestURI, req.method)
}

private fun Logger.logParticipantRegistered(participant: Participant, req: HttpServletRequest) {
    val message = "Participant registered: (id: '{}', IP: '{}')"
    info(message, participant.id, req.realIp)
}

private fun Logger.logParticipantModified(
    req: HttpServletRequest, participant: Long, action: String, subject: String
) {
    val message = "{} participant: (id: '{}', subject: '{}', IP '{}')"
    info(message, action, participant, subject, req.realIp)
}
