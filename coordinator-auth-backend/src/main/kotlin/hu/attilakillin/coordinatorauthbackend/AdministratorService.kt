package hu.attilakillin.coordinatorauthbackend

import hu.attilakillin.coordinatorauthbackend.dal.AdministratorRepository
import org.springframework.stereotype.Service

@Service
class AdministratorService(
    private val repository: AdministratorRepository
) {
}