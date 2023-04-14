import Navbar from '@/components/navbar';
import ArticleCard from '@/components/articles/article-card';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

export default function ArticlesIndex() {
    const [articles, setArticles] = useState([]);

    useEffect(() => {
        fetch(process.env.NEXT_PUBLIC_BACKEND_URL!, { method: 'GET' })
            .then(response => response.json())
            .then(data => setArticles(data));
    }, []);

    const router = useRouter();
    const onClick = (id: number) => {
        router.push('/articles/' + id);
    };

    return (
        <>
            <Head>
                <title>Coordinator</title>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <Navbar />
            <div className='flex-1 container mx-auto px-4'>
                { articles.map((value, index) =>
                    <ArticleCard article={value} key={index} onClick={onClick} />) }
            </div>
        </>
    );
}
