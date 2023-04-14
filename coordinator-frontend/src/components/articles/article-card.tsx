
/* Custom prop type accepting a title and a summary string. */
interface ComponentProps {
    article: {
        id: number,
        title: string,
        summary: string
    },
    onClick?: (id: number) => void
}

/* A component displaying the title and summary of an article. */
export default function ArticleCard(props: ComponentProps) {
    return (
        <div className='p-4 hover:outline hover:outline-3 hover:outline-stone-800'
             onClick={() => props.onClick?.(props.article.id)}>
            <h1 className='text-stone-800 text-2xl mb-4'>{props.article.title}</h1>
            <p className='text-stone-800'>{props.article.summary}</p>
        </div>
    )
}
