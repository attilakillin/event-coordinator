import Navbar from '@/components/blocks/navbar';
import ArticleCard from '@/components/blocks/articles/article-card';
import { useRouter } from 'next/router';
import { useCallback, useEffect, useState } from 'react';
import AppHead from '@/components/builtin/app-head';
import Button from '@/components/builtin/button';
import { ArticleService } from '@/lib/services/article-service';
import { toast } from 'react-toastify';
import Input from '@/components/builtin/input';
import BasicFrame from '@/components/frames/basic-frame';

interface ComponentProps {
    /** The source to be used for populating the list view. */
    source: (query: string) => Promise<any>
}

/**
 * Displays the main list view for a specified article source.
 * Use the various functions of the ArticleService service as a source.
 */
export function ArticleListContent(props: ComponentProps) {
    // Handle state for both the article list and the search keywords.
    const [articles, setArticles] = useState([]);
    const [keywords, setKeywords] = useState('');

    // Generic search function with a provideable query.
    const search = useCallback((query: string) => {
        props.source(query)
            .then(data => setArticles(data))
            .catch(() => toast.error('Hiba történt: A cikkek betöltése nem sikerült!'));
    }, [props]);

    // Search with empty query upon page load.
    useEffect(() => search(''), [search]);

    // Called when either the 'Enter' key, or the search button is pressed in the search bar.
    const handleSearchClick = () => search(keywords);

    // Handle article card clicks.
    const router = useRouter();
    const onClick = (id: number) => router.push(`/articles/${id}`);

    // Return page content.
    return <>
        <div className='flex-1 container mx-auto px-4'>
            <div className='flex flex-row my-4'>
                <Input
                    className='w-full' placeholder='Kulcsszavak...'
                    values={[keywords, setKeywords]} onEnter={handleSearchClick}
                />  
                <Button onClick={handleSearchClick} primary>Keresés</Button>
            </div>

            {
                articles.map((value, i) => <ArticleCard article={value} key={i} onClick={onClick} />)
            }
        </div>
    </>;
}

/**
 * Displays the list of published articles along with a search bar.
 */
export default function ArticleIndex() {
    return <>
        <BasicFrame title='Hírek'>
            <ArticleListContent source={ArticleService.searchPublished} />
        </BasicFrame>
    </>;
}
