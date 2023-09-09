import ParticipantTable from "@/components/blocks/participants/participant-table";
import Button from "@/components/builtin/button";
import Input from "@/components/builtin/input";
import AuthenticatedFrame from "@/components/frames/authenticated-frame";
import BasicFrame from "@/components/frames/basic-frame";
import Id from "@/components/types/id";
import { Participant } from "@/components/types/participant";
import { EventsService } from "@/lib/services/events-service";
import { ParticipantService } from "@/lib/services/participant-service";
import { useRouter } from "next/router";
import { useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";

/**
 * Displays the list of events. Anyone can register to an event, while only
 * administrators can view extra details, or modify an event.
 * 
 * Does not require authentication, although some features are hidden to
 * unauthenticated users.
 */
export default function EventsIndex() {
    // Handle state for both the article list and the search keywords.
    const [events, setEvents] = useState([]);
    const [keywords, setKeywords] = useState('');
    
    // Generic search function with a provideable query.
    const search = useCallback((query: string) => {
        EventsService.search(query)
            .then(data => setEvents(data))
            .catch(() => toast.error('Hiba történt: Az események betöltése nem sikerült!'));
    }, []);

    // Search with empty query upon page load.
    useEffect(() => search(''), [search]);

    // Called when either the 'Enter' key, or the search button is pressed in the search bar.
    const handleSearchClick = () => search(keywords);

    // Handle article card clicks.
    const router = useRouter();
    const onClick = (id: number) => router.push(`/events/${id}`);

    // Render page content.
    return <>
        <BasicFrame title='Események'>
            <div className='flex-1 container mx-auto px-4'>
                <div className='flex flex-row my-4'>
                    <Input
                        className='w-full' placeholder='Kulcsszavak...'
                        values={[keywords, setKeywords]} onEnter={handleSearchClick}
                    />  
                    <Button onClick={handleSearchClick} primary>Keresés</Button>
                </div>

                {
                    
                }
            </div>
        </BasicFrame>
    </>;
}
