import { useRouter } from "next/router";
import { useCallback, useEffect, useState } from "react";
import Button from "@/components/builtin/button";
import { toast } from "react-toastify";
import { AuthenticationStatus, useAuthentication } from "@/lib/hooks/authentication";
import BasicFrame from "@/components/frames/basic-frame";
import { EventService } from "@/lib/services/event-service";
import Input from "@/components/builtin/input";
import { ParticipantService } from "@/lib/services/participant-service";
import ParticipantTable from "@/components/blocks/participants/participant-table";
import { Participant } from "@/components/types/participant";
import Id from "@/components/types/id";
import Link from "next/link";

/**
 * Displays the detailed view of an event. Only displays
 * modification buttons to authenticated users.
 */
export default function EventView() {
    const status = useAuthentication();
    const router = useRouter();

    // Set up state management.
    const [title, setTitle] = useState('');
    const [participantCount, setParticipantCount] = useState(0);
    const [participantList, setParticipantList] = useState<(Participant & Id)[]>([]);

    const [email, setEmail] = useState('');

    // Load event details upon page load.
    const loadContent = useCallback(() => {
        if (typeof router.query.id !== 'undefined') {
            const promise = (status === AuthenticationStatus.SUCCESS)
                ? EventService.getDetails(router.query.id as string)
                    .then(data => {
                        setTitle(data.title);
                        setParticipantList([]);
                        (data.participants as string[]).forEach(email => 
                            ParticipantService.getParticipantByEmail(email)
                                .then(data =>
                                    setParticipantList(list => [...list, data])
                                )
                        );
                    })
                : EventService.getSummary(router.query.id as string)
                    .then(data => {
                        setTitle(data.title);
                        setParticipantCount(data.participants);
                    });

            promise
                .catch(() => {
                    toast.error('Hiba történt: Az esemény betöltése nem sikerült!');
                    router.push('/events');
                });
        }
    }, [router, status]);
    useEffect(() => loadContent(), [loadContent]);

    // Set up edit button handler.
    const handleEditButton = () => router.push('/events/edit/' + router.query.id);

    // Set up delete button handler.
    const handleDeleteButton = () => {
        EventService.remove(router.query.id as string)
            .then(() => {
                toast.success('Sikeres törlés!');
                router.push('/events');
            })
            .catch(() => toast.error('Hiba történt: Az esemény törlése nem sikerült!'));
    };

    // Set up registration handler.
    const handleRegistration = () => {
        EventService.registerToEvent(router.query.id as string, email)
            .then(() => {
                toast.success('Sikeres jelentkezés!');
                loadContent();
            })
            .catch(() => toast.error(<div>Hiba történt: Az eseményre való jelentkezés nem sikerült!<br/>Jelentkezés előtt regisztráljon!</div>));
    }

    // Responsive additional button styling
    const buttonStyles = 'mr-0 md:mr-4 mb-2 md:mb-0';

    // Render final page.
    return (
        <BasicFrame title={title}>
            <div className='flex-1 container mx-auto px-4 flex flex-col'>
                <h1 className='mt-6 mb-4 text-2xl text-theme-800'>{title}</h1>

                <div className='flex-1'>
                    <div className='flex flex-row my-4'>
                        <Input
                            className='w-full' placeholder='Jelentkezek emaillel...'
                            values={[email, setEmail]} onEnter={handleRegistration}
                        />  
                        <Button onClick={handleRegistration} primary>Jelentkezés</Button>
                    </div>
                    <p className='text-theme-800 mb-6 italic text-center'>
                        A jelentkezés előfeltétele a <Link href='/participants/register' className='underline'>Regisztráció</Link> oldalon történő előzetes regisztráció.
                    </p>

                    {
                        (status === AuthenticationStatus.SUCCESS)
                            ? <ParticipantTable
                                data={participantList}
                            />
                            : <p>Jelentkezők száma: {participantCount}</p>
                    }
                </div>

                <div className='flex flex-col md:flex-row justify-end mt-14 mb-4'>
                    {
                        (status == AuthenticationStatus.SUCCESS) && <>
                            <Button onClick={handleEditButton} className={buttonStyles}>Szerkesztés</Button>
                            <Button onClick={handleDeleteButton} className={buttonStyles}>Törlés</Button>
                        </>
                    }
                    <Button onClick={() => router.back()} primary>Vissza</Button>
                </div>
            </div>
        </BasicFrame>
    );
}
