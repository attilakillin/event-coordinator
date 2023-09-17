export interface EventSummary {
    /** The unique ID of the event. Use this to request the whole content of the event. */
    id: number,

    /** The moment an event was created. */
    created: string,

    /** The title of the event. */
    title: string,
    /** The number of participants already registered to the event. */
    participants: number
};

export interface EventDetails {
    /** The unique ID of the event. Use this to request the whole content of the event. */
    id: number,

    /** The moment an event was created. */
    created: string,

    /** The title of the event. */
    title: string,
    /** The list of participants already registered to the event. */
    participants: string[]
};
