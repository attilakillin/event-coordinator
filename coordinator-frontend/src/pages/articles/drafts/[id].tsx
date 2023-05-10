import QuillRenderer from "@/components/blocks/articles/quill-renderer";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import Button from "@/components/builtin/button";
import { ArticleService } from "@/lib/services/article-service";
import { toast } from "react-toastify";
import { AuthenticationStatus, useAuthentication } from "@/lib/hooks/authentication";
import AuthenticatedFrame from "@/components/frames/authenticated-frame";

/**
 * Displays the detailed view of an article. Only displays modification and publication
 * buttons to authenticated users.
 * 
 * Requires authentication.
 */
export default function ArticleDraftView() {
    const status = useAuthentication();
    const router = useRouter();

    // Set up state management.
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');

    // Load article details upon page load.
    useEffect(() => {
        if (typeof router.query.id !== 'undefined') {
            ArticleService.get(router.query.id as string)
                .then(data => {
                    setTitle(data.title);
                    setContent(data.content);
                })
                .catch(() => {
                    toast.error('Hiba történt: A cikk betöltése nem sikerült!');
                    router.push('/articles/drafts');
                });
        }
    }, [router, router.query.id]);

    // Set up publish button handler.
    const handlePublishButton = () => {
        ArticleService.publish(router.query.id as string)
            .then(() => {
                toast.success('Sikeres publikálás!');
                router.push('/articles');
            })
            .catch(() => toast.error('Hiba történt: A cikk publikálása nem sikerült!'));
    }

    // Set up edit button handler.
    const handleEditButton = () => router.push('/articles/edit/' + router.query.id);

    // Set up delete button handler.
    const handleDeleteButton = () => {
        ArticleService.remove(router.query.id! as string)
            .then(() => {
                toast.success('Sikeres törlés!');
                router.push('/articles/drafts');
            })
            .catch(() => toast.error('Hiba történt: A cikk törlése nem sikerült!'));
    };

    // Responsive additional button styling
    const buttonStyles = 'mr-0 md:mr-4 mb-2 md:mb-0';

    // Render final page.
    return (
        <AuthenticatedFrame title={title}>
            <div className='flex-1 container mx-auto px-4 flex flex-col'>
                <h1 className='mt-6 mb-4 text-2xl text-theme-800'>{title}</h1>

                <div className='flex-1'>
                    <QuillRenderer content={content} />
                </div>

                <div className='flex flex-col md:flex-row justify-end mt-14 mb-4'>
                    {
                        (status == AuthenticationStatus.SUCCESS) && <>
                            <Button onClick={handlePublishButton} className={buttonStyles}>Publikálás</Button>
                            <Button onClick={handleEditButton} className={buttonStyles}>Szerkesztés</Button>
                            <Button onClick={handleDeleteButton} className={buttonStyles}>Törlés</Button>
                        </>
                    }
                    <Button onClick={() => router.back()} primary>Vissza</Button>
                </div>
            </div>
        </AuthenticatedFrame>
    );
}
