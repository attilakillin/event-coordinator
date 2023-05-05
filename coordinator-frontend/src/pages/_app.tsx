import '@/styles/globals.css';
import type { AppProps } from 'next/app';
import { Inter } from 'next/font/google';
import 'react-toastify/dist/ReactToastify.css';
import { Slide, ToastContainer } from 'react-toastify';

const inter = Inter({ subsets: ['latin'] });

/**
 * The outermost wrapper of the application. Contains a full-height wrapper
 * for children, and a toast container that is shared between pages.
 */
export default function App({ Component, pageProps }: AppProps) {
    return <>
        <div className={inter.className + ' h-full flex flex-col'}>
            <Component {...pageProps} />
        </div>
        <ToastContainer
            position='bottom-left' autoClose={2000} transition={Slide}
            hideProgressBar closeOnClick newestOnTop
        />
    </>;
}
