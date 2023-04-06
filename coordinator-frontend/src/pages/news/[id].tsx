import Navbar from "@/components/navbar";
import QuillRenderer from "@/components/news/quill-renderer";
import Head from "next/head";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";

/* Custom prop type accepting an article id. */
interface ComponentProps {
    id: number
}

export default function NewsView(props: ComponentProps) {
    /* Tailwind styling classes. */
    const primary = 'hover:outline hover:outline-3 hover:outline-stone-800 px-8 py-3 text-lg bg-stone-800 text-stone-100';
    const secondary = 'hover:outline hover:outline-3 hover:outline-stone-800 px-8 py-3 text-lg text-stone-800 mr-4';
    
    const router = useRouter();

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');

    useEffect(() => {
        fetch('http://localhost:8080/' + router.query.id, { method: 'GET' })
            .then(response => response.json())
            .then(data => {
                setTitle(data.title);
                setContent(data.content);
            });
    }, []);

    return (
        <>
            <Head>
                <title>{title + ' - Coordinator'}</title>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            
            <Navbar />

            <div className='flex-1 container mx-auto px-4'>
                <div className='h-1/2'>
                    <h1 className='text-stone-800 text-2xl mt-6 mb-4'>{title}</h1>
                    <div className='h-4/5 overflow-auto'>
                        <QuillRenderer content={content} />
                    </div>
                </div>

                <div className='flex justify-end mt-14 mb-4'>
                    <button className={secondary} onClick={() => router.back()}>Vissza</button>
                </div>
            </div>
        </>
    );
}
