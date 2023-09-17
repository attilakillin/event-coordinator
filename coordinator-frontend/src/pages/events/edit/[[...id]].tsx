import { useRouter } from 'next/router';
import { useCallback, useEffect, useState } from 'react';
import Button from '@/components/builtin/button';
import { toast } from 'react-toastify';
import AuthenticatedFrame from '@/components/frames/authenticated-frame';
import Input from '@/components/builtin/input';
import { EventService } from '@/lib/services/event-service';
import { ParticipantService } from '@/lib/services/participant-service';
import { Participant } from '@/components/types/participant';
import Id from '@/components/types/id';
import ParticipantTable from '@/components/blocks/participants/participant-table';

/**
 * Displays the main page for event editing and creation. If the path URL contains an ID,
 * then the save action is interpreted as an update request, else as a save request.
 * 
 * Requires authentication.
 */
export default function EventCreate() {
    const router = useRouter();

    // Initialize state management.
    const [title, setTitle] = useState('');
    const [participants, setParticipants] = useState<(Participant & Id)[]>([]);

    // Find out whether we're updating or creating an article.
    const [updating, setUpdating] = useState(false);
    useEffect(() => {
        setUpdating(typeof router.query.id !== 'undefined');
    }, [router.query]);

    // If we are editing an already written article, we need to load its content from the server.
    const loadContent = useCallback(() => {
        if (!updating) return;
        
        EventService.getDetails(router.query.id as string)
            .then(data => {
                setTitle(data.title);
                (data.participants as string[]).forEach(email => 
                    ParticipantService.getParticipantByEmail(email)
                        .then(data =>
                            setParticipants(list => [...list, data])
                        )
                );
            })
            .catch(() => toast.error('Hiba történt: Az esemény betöltése nem sikerült!'));
    }, [updating, router.query.id]);
    useEffect(() => loadContent(), [loadContent]);

    // Save button click handler. Can handle both save- and update requests.
    const handleSaveClick = () => {
        const request = (updating)
            ? EventService.put(router.query.id as string, title)
            : EventService.post(title);

        request
            .then(() => {
                toast.success('Sikeres mentés!');
                router.push('/events');
            })
            .catch(() => {
                toast.error(<div>Az esemény mentése nem sikerült!</div>);
            });
    };

    // Delete registration button click handler.
    const handleRegistrationDeleteClick = (id: string) => {
        EventService.remove(id)
            .then(() => {
                loadContent();
                toast.success('Résztvevő jelentkezése sikeresen törölve!');
            })
            .catch(() => toast.error('Hiba történt: A résztvevő jelentkezésének törlése nem sikerült!'));
    }

    // Generate page layout.
    return (
        <AuthenticatedFrame title='Esemény szerkesztése'>
            <div className='flex-1 container mx-auto px-4 flex flex-col'>
                <h1 className='mt-6 mb-4 text-2xl text-theme-800'>Új esemény</h1>

                <div className='mb-2'>
                    <Input className='w-full' values={[title, setTitle]} placeholder='Esemény címe...' />
                </div>

                <div className='flex-1 flex flex-col'>
                    <div className='flex-1'>
                        <ParticipantTable
                            data={participants}
                            action={({ name: 'Törlés', callback: handleRegistrationDeleteClick })}
                        />
                    </div>

                    <div className='flex flex-col md:flex-row justify-end mt-20 md:mt-14 mb-4'>
                        <Button onClick={handleSaveClick} primary>Mentés</Button>
                    </div>
                </div>
            </div>
        </AuthenticatedFrame>
    );
}
