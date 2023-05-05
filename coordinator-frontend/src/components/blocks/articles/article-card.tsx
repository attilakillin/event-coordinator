import ArticleSummary from "@/components/types/article"

interface ComponentProps {
    /** The summary of the article, bundled with other metadata. */
    article: ArticleSummary,
    /** Click handler for the whole card. Use this to open a page when the card is clicked. */
    onClick?: (id: number) => void
};

/** 
 * A clickable component displaying the title and summary of an article.
 * Can be used to redirect the user to a page upon clicking anywhere on the card.
 */
export default function ArticleCard(props: ComponentProps) {
    const styles = 'bg-theme-100 p-8 mb-4 hover:outline hover:outline-3 hover:outline-theme-800';

    return (
        <div className={styles} onClick={() => props.onClick?.(props.article.id)}>
            <h1 className='text-theme-800 text-2xl mb-4'>{props.article.title}</h1>
            <p className='text-theme-800'>{props.article.summary}</p>
        </div>
    );
};
