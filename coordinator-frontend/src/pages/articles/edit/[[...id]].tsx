import Navbar from '@/components/blocks/navbar';
import QuillRenderer from '@/components/blocks/articles/quill-renderer';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import 'react-quill/dist/quill.snow.css';
import Button from '@/components/builtin/button';
import { ArticleService } from '@/lib/services/article-service';
import { toast } from 'react-toastify';
import AuthenticatedFrame from '@/components/frames/authenticated-frame';
import Input from '@/components/builtin/input';
import BasicFrame from '@/components/frames/basic-frame';

// Import QuillJS without server-side rendering.
const ReactQuill = dynamic(() => import('react-quill'), { ssr: false });

// Customize QuillJS appearance and behaviour.
const quillOptions = {
    modules: {
        toolbar: [
            [{'header': 1}, {'header': 2}, {'font': []}],

            ['bold', 'italic', 'underline'],
            [{'color': []}, {'background': []}],
            ['link'],
            [{'list': 'ordered'}, {'list': 'bullet'}],
            ['blockquote', {'indent': '-1'}, {'indent': '+1'}],

            ['clean']
        ]
    },
    formats: [
        'background', 'bold', 'color', 'font', 'italic', 'link', 'size', 'underline',
        'blockquote', 'header', 'indent', 'list'
    ]
};

/**
 * Displays the main page for article editing and creation. If the path URL contains an ID,
 * then the save action is interpreted as an update request, else as a save request.
 * 
 * Requires authentication.
 */
export default function ArticleCreate() {
    const router = useRouter();

    // Initialize state management.
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [published, setPublished] = useState(false);
    const [isPreview, setIsPreview] = useState(false);

    // Find out whether we're updating or creating an article.
    const [updating, setUpdating] = useState(false);
    useEffect(() => {
        setUpdating(typeof router.query.id !== 'undefined');
    }, [router.query]);

    // If we are editing an already written article, we need to load its content from the server.
    useEffect(() => {
        if (!updating) return;
        
        ArticleService.get(router.query.id![0])
            .then(data => {
                setTitle(data.title);
                setContent(data.content);
                setPublished(data.published);
            })
            .catch(() => toast.error('Hiba történt: A cikk betöltése nem sikerült!'));
    }, [updating, router.query.id]);

    // Preview button click handler.
    const handlePreviewClick = () => setIsPreview(value => !value);

    // Save button click handler. Can handle both save- and update requests.
    const handleSaveClick = () => {
        const target = (published) ? '/articles' : '/articles/drafts';

        const request = (updating)
            ? ArticleService.put(router.query.id![0], title, content)
            : ArticleService.post(title, content);

        request
            .then(() => {
                toast.success('Sikeres mentés!');
                router.push(target);
            })
            .catch(() => {
                toast.error(<div>A cikk mentése nem sikerült!<br />A cím és a szöveg mezők nem lehetnek üresek!</div>);
            });
    };

    // Generate page layout.
    return (
        <AuthenticatedFrame title='Bejegyzés szerkesztése'>
            <div className='flex-1 container mx-auto px-4 flex flex-col'>
                <h1 className='mt-6 mb-4 text-2xl text-theme-800'>Új bejegyzés</h1>

                <div className='mb-2'>
                    {
                        isPreview
                        ? <h1 className='text-theme-800 text-xl'>{title}</h1>
                        : <Input className='w-full' values={[title, setTitle]} placeholder='Bejegyzés címe...' />
                    }
                </div>

                <div className='flex-1 flex flex-col'>
                    <div className='flex-1'>
                        {
                            isPreview
                            ? <QuillRenderer content={content} />
                            : <ReactQuill
                                theme='snow' className='h-full' placeholder='Bejegyzés szövege...'
                                modules={quillOptions.modules} formats={quillOptions.formats}
                                value={content} onChange={setContent}
                            />
                        }
                    </div>

                    <div className='flex justify-end mt-14 mb-4'>
                        <Button onClick={handlePreviewClick} className='mr-4'>
                            {isPreview ? 'Szerkesztés' : 'Előnézet'}
                        </Button>
                        <Button onClick={handleSaveClick} primary>Mentés</Button>
                    </div>
                </div>
            </div>
        </AuthenticatedFrame>
    );
}
