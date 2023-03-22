import Navbar from '@/components/navbar';
import QuillRenderer from '@/components/quill-renderer';
import dynamic from 'next/dynamic';
import Head from 'next/head';
import { useState } from 'react';
import 'react-quill/dist/quill.snow.css';

/* QuillJS has to be loaded client-side, we can't render it on the server. */
const ReactQuill = dynamic(() => import('react-quill'), { ssr: false });

export default function NewsCreate() {
    /* Tailwind styling classes. */
    const primary = 'hover:outline hover:outline-3 hover:outline-stone-800 px-8 py-3 text-lg bg-stone-800 text-stone-100';
    const secondary = 'hover:outline hover:outline-3 hover:outline-stone-800 px-8 py-3 text-lg text-stone-800 mr-4';

    /* Initialize state management. */
    const [title, setTitle] = useState('');
    const [text, setText] = useState('');
    const [isPreview, setIsPreview] = useState(false);

    /* Editor change handler (as ReactQuill doesn't output Quill Deltas by default). */
    const handleEditorChange = (content: any, delta: any, source: any, editor: any) => {
        setText(content);
    }

    /* Preview button click handler. */
    const handlePreviewClick = () => {
        setIsPreview(value => !value);
    };

    /* Dummy save button click handler. */
    const handleSaveClickDummy = () => {
        alert(title + '\n' + text);
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
                            <QuillRenderer content={text} />
                        </div>
                    </div>
                    : <div className='h-1/2'>
                        <input type='text' className='border border-stone-300 block w-full px-4 py-1 mb-2'
                               value={title} onChange={e => setTitle(e.target.value)}
                               placeholder='Bejegyzés címe...' />
                        <div className='h-4/5'> 
                            <ReactQuill theme='snow' className='h-full' value={text} modules={modules}
                                        onChange={handleEditorChange} placeholder='Bejegyzés szövege...' />
                        </div>
                    </div>
                }

                <div className='flex justify-end mt-14 mb-4'>
                    <button className={secondary} onClick={handlePreviewClick}>
                        {isPreview ? 'Szerkesztés' : 'Előnézet'}
                        </button>
                    <button className={primary} onClick={handleSaveClickDummy}>Mentés</button>
                </div>
            </div>
        </>
    );
}
