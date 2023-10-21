package hu.attilakillin.coordinatoreventsbackend.dal

/**
 * The current status related to a participant's check-in.
 */
enum class CheckinStatus {
    /** Status unknown, the default. */
    UNKNOWN,
    /** Checked in. */
    CHECKED_IN,
    /** Declined, will not check in. */
    DECLINED
}
