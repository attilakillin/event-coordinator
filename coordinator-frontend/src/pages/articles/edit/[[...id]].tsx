import Navbar from '@/components/navbar';
import QuillRenderer from '@/components/articles/quill-renderer';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import 'react-quill/dist/quill.snow.css';
import Button from '@/components/builtin/button';
import AppHead from '@/components/builtin/app_head';
import { ArticleService } from '@/services/article-service';
import { toast } from 'react-toastify';

/* QuillJS has to be loaded client-side, we can't render it on the server. */
const ReactQuill = dynamic(() => import('react-quill'), { ssr: false });

export default function ArticlesCreate() {
    const router = useRouter();

    /* Initialize state management. */
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [published, setPublished] = useState(false);
    const [isPreview, setIsPreview] = useState(false);

    /* If we are editing an already written article, load its content from the server. */
    useEffect(() => {
        if (typeof router.query.id !== 'undefined') {
            ArticleService.get(router.query.id![0])
                .then(data => {
                    setTitle(data.title);
                    setContent(data.content);
                    setPublished(data.published);
                })
                .catch(_ => toast.error('Hiba történt: A cikk betöltése nem sikerült!'));
        }
    }, [router.query]);

    /* Preview button click handler. */
    const handlePreviewClick = () => {
        setIsPreview(value => !value);
    };

    /* Dummy save button click handler. */
    const handleSaveClick = () => {
        const target = (published) ? '/articles' : '/articles/drafts';

        if (typeof router.query.id !== 'undefined') {
            ArticleService.put(router.query.id![0], title, content)
                .then(_ => router.push(target))
                .catch(_ => toast.error('Hiba történt: A cikk mentése nem sikerült!'));
        } else {
            ArticleService.post(title, content)
                .then(_ => router.push(target))
                .catch(_ => toast.error('Hiba történt: A cikk mentése nem sikerült!'));
        }
    };

    /* Customize QuillJS toolbar module. */
    const modules = {
        toolbar: [
            [{'header': 1}, {'header': 2}, {'font': []}],

            ['bold', 'italic', 'underline'],
            [{'color': []}, {'background': []}],
            ['link'],
            [{'list': 'ordered'}, {'list': 'bullet'}],
            ['blockquote', {'indent': '-1'}, {'indent': '+1'}],

            ['clean']
        ]
    };

    const formats = [
        'background', 'bold', 'color', 'font', 'italic', 'link', 'size', 'underline',
        'blockquote', 'header', 'indent', 'list'
    ];

    /* Generate page markup. */
    return (
        <>
            <AppHead title='Új bejegyzés - Coordinator' />
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
                                        onChange={setContent} formats={formats} placeholder='Bejegyzés szövege...' />
                        </div>
                    </div>
                }

                <div className='flex justify-end mt-14 mb-4'>
                    <Button onClick={handlePreviewClick} className='mr-4'>
                        {isPreview ? 'Szerkesztés' : 'Előnézet'}
                    </Button>
                    <Button onClick={handleSaveClick} primary>Mentés</Button>
                </div>
            </div>
        </>
    );
}
