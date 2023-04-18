package hu.attilakillin.coordinatorauthbackend

import org.springframework.stereotype.Controller

@Controller
class AdministratorController(
    private val service: AdministratorService
) {
}