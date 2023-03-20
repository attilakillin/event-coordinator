import Navbar from '@/components/navbar';
import dynamic from 'next/dynamic';
import Head from 'next/head';
import 'react-quill/dist/quill.snow.css';

const ReactQuill = dynamic(() => import('react-quill'), { ssr: false });

export default function NewsCreate() {
    return (
        <>
            <Head>
                <title>Coordinator</title>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <Navbar />
            <div className='flex-1 w-full flex items-center justify-center'>
                <ReactQuill theme='snow' />
            </div>
        </>
    );
}
