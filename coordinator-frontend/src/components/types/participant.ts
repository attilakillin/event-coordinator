interface Participant {
    /** The last name of the participant. */
    lastName: string,
    /** The first name of the participant. */
    firstName: string,

    /** The email of the participant. Required in order to contact them. */
    email: string,
    /** The real-world address of the participant, not required. */
    address: string,
    /** The phone number of the participant, not required. */
    phoneNumber: string,

    /** Additional notes the participant wished to share. Not required. */
    notes: string
}

export type { Participant }
