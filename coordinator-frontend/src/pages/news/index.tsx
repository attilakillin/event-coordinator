import Navbar from '@/components/navbar';
import ArticleCard from '@/components/news/article-card';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

export default function NewsIndex() {
    const [articles, setArticles] = useState([]);

    useEffect(() => {
        fetch('http://localhost:8080', { method: 'GET' })
            .then(response => response.json())
            .then(data => setArticles(data));
    }, []);

    const router = useRouter();
    const onClick = (id: number) => {
        router.push('/news/' + id);
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
