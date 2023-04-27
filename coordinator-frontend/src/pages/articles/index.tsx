import Navbar from '@/components/navbar';
import ArticleCard from '@/components/articles/article-card';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import AppHead from '@/components/builtin/app_head';
import Button from '@/components/builtin/button';
import { ArticleService } from '@/services/article-service';
import { toast } from 'react-toastify';

export default function ArticlesIndex() {
    const [articles, setArticles] = useState([]);
    const [keywords, setKeywords] = useState('');

    const handleSearchClick = () => {
        ArticleService.search(keywords)
            .then(data => setArticles(data))
            .catch(_ => toast.error('Hiba történt: A cikkek betöltése nem sikerült!'));
    }

    useEffect(handleSearchClick, []);

    const router = useRouter();
    const onClick = (id: number) => {
        router.push('/articles/' + id);
    };

    return (
        <>
            <AppHead title='Hírek - Coordinator' />
            <Navbar />

            <div className='flex-1 container mx-auto px-4'>
                <div className='flex flex-row mt-4 mb-4'>
                    <input className='block w-full text-lg placeholder:italic placeholder:text-stone-400 border border-stone-300 px-2 py-2' type='text'
                           placeholder='Kulcsszavak...' value={keywords} onChange={e => setKeywords(e.target.value)}
                           onKeyDown={e => (e.key === 'Enter') && handleSearchClick()} />
                    
                    <Button onClick={handleSearchClick} primary>Keresés</Button>
                </div>
                { articles.map((value, index) =>
                    <ArticleCard article={value} key={index} onClick={onClick} />) }
            </div>
        </>
    );
}
