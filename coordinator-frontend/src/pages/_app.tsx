import '@/styles/globals.css';
import type { AppProps } from 'next/app';
import { Inter } from 'next/font/google';
import 'react-toastify/dist/ReactToastify.css';
import { Slide, ToastContainer } from 'react-toastify';

const inter = Inter({ subsets: ['latin'] });

export default function App({ Component, pageProps }: AppProps) {
    return <>
        <main className={inter.className + ' h-full flex flex-col'}>
            <Component {...pageProps} />
        </main>
        <ToastContainer
            position='bottom-left' autoClose={2000} transition={Slide}
            hideProgressBar closeOnClick newestOnTop
        />
    </>;
}
