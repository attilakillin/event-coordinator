import Navbar from '@/components/navbar';
import QuillRenderer from '@/components/news/quill-renderer';
import dynamic from 'next/dynamic';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import 'react-quill/dist/quill.snow.css';

/* QuillJS has to be loaded client-side, we can't render it on the server. */
const ReactQuill = dynamic(() => import('react-quill'), { ssr: false });

export default function NewsCreate() {
    /* Tailwind styling classes. */
    const primary = 'hover:outline hover:outline-3 hover:outline-stone-800 px-8 py-3 text-lg bg-stone-800 text-stone-100';
    const secondary = 'hover:outline hover:outline-3 hover:outline-stone-800 px-8 py-3 text-lg text-stone-800 mr-4';

    const router = useRouter();

    /* Initialize state management. */
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [isPreview, setIsPreview] = useState(false);

    /* If we are editing an already written article, load its content from the server. */
    if (typeof router.query.id !== 'undefined') {
        useEffect(() => {
            fetch('http://localhost:8080/' + router.query.id![0], { method: 'GET' })
                .then(response => response.json())
                .then(data => {
                    setTitle(data.title);
                    setContent(data.content);
                });
        }, []);
    }

    /* Preview button click handler. */
    const handlePreviewClick = () => {
        setIsPreview(value => !value);
    };

    /* Dummy save button click handler. */
    const handleSaveClick = () => {
        if (typeof router.query.id !== 'undefined') {
            fetch('http://localhost:8080/' + router.query.id![0], {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title: title, content: content })
            }).then(response => {
                if (response.status === 200) {
                    router.push('/news/');
                }
            });
        } else {
            fetch('http://localhost:8080', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title: title, content: content })
            }).then(response => {
                if (response.status === 201) {
                    router.push('/news/');
                }
            });
        }
    };

    /* Customize QuillJS toolbar module. */
    const modules = {
        toolbar: [
            [{'header': 1}, {'header': 2}, {'font': []}],

            ['bold', 'italic', 'underline'],
            [{'color': []}, {'background': []}],
            ['link', 'image'],
            [{'list': 'ordered'}, {'list': 'bullet'}],
            ['blockquote', {'indent': '-1'}, {'indent': '+1'}],

            ['clean']
        ]
    };

    /* Generate page markup. */
    return (
        <>
            <Head>
                <title>Új bejegyzés - Coordinator</title>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            
            <Navbar />

            <div className='flex-1 container mx-auto px-4'>
                <h1 className='text-stone-800 text-2xl mt-6 mb-4'>Új bejegyzés</h1>

                {
                    isPreview
                    ? <div className='h-1/2'>
                        <h1 className='text-stone-800 text-xl mt-6 mb-2'>{title}</h1>
                        <div className='h-4/5 overflow-auto'>
                            <QuillRenderer content={content} />
                        </div>
                    </div>
                    : <div className='h-1/2'>
                        <input type='text' className='border border-stone-300 block w-full px-4 py-1 mb-2'
                               value={title} onChange={e => setTitle(e.target.value)}
                               placeholder='Bejegyzés címe...' />
                        <div className='h-4/5'> 
                            <ReactQuill theme='snow' className='h-full' value={content} modules={modules}
                                        onChange={setContent} placeholder='Bejegyzés szövege...' />
                        </div>
                    </div>
                }

                <div className='flex justify-end mt-14 mb-4'>
                    <button className={secondary} onClick={handlePreviewClick}>
                        {isPreview ? 'Szerkesztés' : 'Előnézet'}
                        </button>
                    <button className={primary} onClick={handleSaveClick}>Mentés</button>
                </div>
            </div>
        </>
    );
}
