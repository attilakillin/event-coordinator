import Navbar from "@/components/navbar";
import QuillRenderer from "@/components/articles/quill-renderer";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import Button from "@/components/builtin/button";
import AppHead from "@/components/builtin/app_head";
import { ArticleService } from "@/services/article-service";
import { toast } from "react-toastify";
import { AuthService } from "@/services/auth-service";

export default function ArticlesView() {
    const router = useRouter();

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');

    useEffect(() => {
        if (typeof router.query.id !== 'undefined') {
            ArticleService.get(router.query.id![0])
                .then(data => { setTitle(data.title); setContent(data.content); })
                .catch(_ => toast.error('Hiba történt: A cikk betöltése nem sikerült!'));
        }
    }, [router.query.id]);

    const [loggedIn, setLoggedIn] = useState(false);
    useEffect(() => {
        AuthService.validate()
            .then(_ => setLoggedIn(true))
            .catch(_ => setLoggedIn(false));
    }, []);

    const handleEditButton = () => {
        router.push('/articles/edit/' + router.query.id);
    };

    const handleDeleteButton = () => {
        ArticleService.remove(router.query.id![0])
            .then(() => router.back())
            .catch(_ => toast.error('Hiba történt: A cikk törlése nem sikerült!'));
    };

    return (
        <>
            <AppHead title={title + ' - Coordinator'} />
            <Navbar />

            <div className='flex-1 container mx-auto px-4'>
                <div className='h-1/2'>
                    <h1 className='text-stone-800 text-2xl mt-6 mb-4'>{title}</h1>
                    <div className='h-4/5 overflow-auto'>
                        <QuillRenderer content={content} />
                    </div>
                </div>

                <div className='flex justify-end mt-14 mb-4'>
                    {(loggedIn) && <>
                        <Button onClick={handleEditButton} className='mr-4'>Szerkesztés</Button>
                        <Button onClick={handleDeleteButton} className='mr-4'>Törlés</Button>
                    </>}
                    <Button onClick={() => router.back()} primary>Vissza</Button>
                </div>
            </div>
        </>
    );
}
