package hu.attilakillin.coordinatoreventsbackend.controllers

import hu.attilakillin.coordinatoreventsbackend.dto.CheckinDTO
import hu.attilakillin.coordinatoreventsbackend.dto.toDTO
import hu.attilakillin.coordinatoreventsbackend.exceptions.BadRequestException
import hu.attilakillin.coordinatoreventsbackend.exceptions.ForbiddenException
import hu.attilakillin.coordinatoreventsbackend.services.AuthService
import hu.attilakillin.coordinatoreventsbackend.services.CheckinService
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/checkin")
class CheckinController(
    private val checkinService: CheckinService,
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
    fun isTokenValid(token: String?, req: HttpServletRequest?): Boolean {
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

    @GetMapping("/{id}")
    fun retrieveAll(
        @PathVariable id: String,
        @RequestHeader("Auth-Token") token: String?,
        req: HttpServletRequest
    ): ResponseEntity<List<CheckinDTO>> {
        if (!isTokenValid(token, req)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val validId = id.toLongOrNull()
            ?: return ResponseEntity.badRequest().build()

        val list = checkinService.getCheckinsForEvent(validId)
            ?: return ResponseEntity.badRequest().build()

        return ResponseEntity.ok(list)
    }

    @MessageMapping("/update")
    @SendTo("/topic/checkins")
    fun updateOne(
        @Payload dto: CheckinDTO,
        @Header("Auth-Token") token: String?
    ): CheckinDTO {
        if (!isTokenValid(token, null)) {
            throw ForbiddenException()
        }

        val success = checkinService.updateCheckinForEvent(dto)
        if (!success) {
            throw BadRequestException()
        }

        return dto
    }
}

/* Logging extension functions. */

private val HttpServletRequest.realIp get() = getHeader("X-Real-Ip")

private fun Logger.logTokenNotVerified(req: HttpServletRequest?) {
    val message = "Token verification failed: (IP: '{}', requested path: '{}', method: '{}')"
    info(message, req?.realIp ?: "", req?.requestURI ?: "", req?.method ?: "")
}

private fun Logger.logEventModified(req: HttpServletRequest, event: Long, action: String, subject: String) {
    val message = "{} event: (id: '{}', subject: '{}', IP '{}')"
    info(message, action, event, subject, req.realIp)
}

private fun Logger.logRegistrationUnsuccessful(req: HttpServletRequest, event: Long, email: String) {
    val message = "Event registration unsuccessful: (event: '{}', email: '{}', IP '{}')"
    info(message, event, email, req.realIp)
}

private fun Logger.logUnregistrationUnsuccessful(req: HttpServletRequest, event: Long, email: String) {
    val message = "Event unregistration unsuccessful: (event: '{}', email: '{}', IP '{}')"
    info(message, event, email, req.realIp)
}
