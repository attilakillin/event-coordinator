export default interface ArticleSummary {
    /** The unique ID of the article. Use this to request the whole content of the article. */
    id: number,

    /** The moment a draft article was created, or a published article published. */
    created: string,
    /** Whether the article is already published, or not. */
    published: boolean,

    /** The title of the article. */
    title: string,
    /** A plain-text summary of the article. */
    summary: string
};
