import Navbar from '@/components/navbar';
import Head from 'next/head';

export default function NewsIndex() {
    return (
        <>
            <Head>
                <title>Coordinator</title>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <Navbar />
            <div className='flex-1 w-full flex items-center justify-center'>
                Hírek
            </div>
        </>
    );
}
