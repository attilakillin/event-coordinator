import Navbar from '@/components/navbar'
import Head from 'next/head'

export default function Index() {
    return (
        <>
            <Head>
                <title>Coordinator</title>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <Navbar />
            <div className='flex justify-center items-center h-full'>
                <p className='text-indigo-400'>Coordinator Frontend</p>
            </div>
        </>
    )
}
