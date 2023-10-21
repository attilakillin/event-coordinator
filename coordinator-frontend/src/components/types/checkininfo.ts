import { CheckinStatus } from "./checkinstatus"

interface CheckinInfo extends CheckinStatus {
    /** The last name of the participant. */
    lastName: string,
    /** The first name of the participant. */
    firstName: string
}

export type { CheckinInfo }
