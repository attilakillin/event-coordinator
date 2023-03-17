import Head from 'next/head'
import { Inter } from 'next/font/google'

const inter = Inter({ subsets: ['latin'] })

export default function Home() {
    return (
        <>
            <Head>
                <title>Coordinator</title>
                <meta name='viewport' content='width=device-width, initial-scale=1' />
            </Head>
            <main className='flex justify-center items-center h-full'>
                <div className=''>
                    <p className={inter.className + ' text-indigo-400'}>Coordinator Frontend</p>
                </div>
            </main>
        </>
    )
}
