import Navbar from '@/components/navbar';
import dynamic from 'next/dynamic';
import Head from 'next/head';
import { useState } from 'react';
import 'react-quill/dist/quill.snow.css';

/* QuillJS has to be loaded client-side, we can't render it on the server. */
const ReactQuill = dynamic(() => import('react-quill'), { ssr: false });

export default function NewsCreate() {
    /* Tailwind styling classes. */
    const primary = 'hover:outline hover:outline-3 hover:outline-stone-700 px-8 py-3 text-lg bg-stone-700 text-stone-100';
    const secondary = 'hover:outline hover:outline-3 hover:outline-stone-700 px-8 py-3 text-lg text-stone-700 mr-4';

    /* Initialize state management. */
    const [text, setText] = useState('');
    const [isPreview, setIsPreview] = useState(false);

    /* Preview button click handler. */
    const onPreviewClick = () => {
        setIsPreview(value => !value);
    };

    /* Dummy save button click handler. */
    const onSaveClickDummy = () => {
        alert(text);
    };

    /* Customize QuillJS toolbar module. */
    const modules = {
        toolbar: [
            [{'font': []}],

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
                <h1 className='text-stone-700 text-2xl mt-6 mb-4'>Új bejegyzés</h1>
                <div className='h-3/5'>
                    {
                        isPreview
                        ? <div dangerouslySetInnerHTML={{__html: text}}></div> /* TODO: This is unsafe (XSS). */
                        : <ReactQuill theme='snow' className='h-full' value={text} onChange={setText}
                                      modules={modules} placeholder='Bejegyzés szövege...' />
                    }
                </div>
                <div className='flex justify-end mt-14 mb-4'>
                    <button className={secondary} onClick={onPreviewClick}>Előnézet</button>
                    <button className={primary} onClick={onSaveClickDummy}>Mentés</button>
                </div>
            </div>
        </>
    );
}
