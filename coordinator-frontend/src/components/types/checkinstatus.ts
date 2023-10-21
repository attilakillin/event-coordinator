interface CheckinStatus {
    /** The ID of the event. */
    eventId: number,
    /** The email of the participant. */
    email: string,
    /** The check-in status of the participant. */
    status: string
}

export type { CheckinStatus }
