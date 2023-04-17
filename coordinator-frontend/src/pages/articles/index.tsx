import Navbar from '@/components/navbar';
import ArticleCard from '@/components/articles/article-card';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

export default function ArticlesIndex() {
    const [articles, setArticles] = useState([]);
    const [filter, setFilter] = useState('');

    useEffect(() => {
        fetch(process.env.NEXT_PUBLIC_BACKEND_URL!, { method: 'GET' })
            .then(response => response.json())
            .then(data => setArticles(data))
            .catch(error => console.log('Could not reach backend!'));
    }, []);

    const router = useRouter();
    const onClick = (id: number) => {
        router.push('/articles/' + id);
    };

    const onFilter = () => {
        fetch(process.env.NEXT_PUBLIC_BACKEND_URL! + '?keywords=' + encodeURIComponent(filter), { method: 'GET' })
            .then(response => response.json())
            .then(data => setArticles(data))
            .catch(error => console.log('Could not reach backend!'));
    }

    return (
        <>
            <Head>
                <title>Coordinator</title>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <Navbar />
            <div className='flex-1 container mx-auto px-4'>
                <div className='flex flex-row mt-4 mb-4'>
                    <input className='block basis-4/5 text-lg placeholder:italic placeholder:text-stone-400 border border-stone-300 px-2 py-2' type='text'
                           placeholder='Kulcsszavak...' value={filter} onChange={e => setFilter(e.target.value)} />
                    <button className='basis-1/5 bg-stone-800 text-stone-100 hover:outline hover:outline-3 hover:outline-stone-800 px-6 py-2 text-lg'
                            onClick={onFilter}>Keresés</button>
                </div>
                { articles.map((value, index) =>
                    <ArticleCard article={value} key={index} onClick={onClick} />) }
            </div>
        </>
    );
}
