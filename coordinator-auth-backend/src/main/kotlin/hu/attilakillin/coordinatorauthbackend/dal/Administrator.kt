package hu.attilakillin.coordinatorauthbackend.dal

/**
 * The main domain specific entity of the application:
 * An administrator, with a username and a passwordd.
 */
data class Administrator(
    /** Username of the administrator. */
    var username: String,
    /** Password of the administrator. */
    var password: String
)
