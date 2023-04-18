package hu.attilakillin.coordinatorauthbackend.dal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdministratorRepository : JpaRepository<Administrator, Long> {
}
