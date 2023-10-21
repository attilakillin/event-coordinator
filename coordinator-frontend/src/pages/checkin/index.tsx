import CheckinCard from "@/components/blocks/checkin/checkin-card";
import Input from "@/components/builtin/input";
import AuthenticatedFrame from "@/components/frames/authenticated-frame";
import { CheckinInfo } from "@/components/types/checkininfo";
import { CheckinStatus } from "@/components/types/checkinstatus";
import { EventSummary } from "@/components/types/event";
import { CheckinService } from "@/lib/services/checkin-service";
import { EventService } from "@/lib/services/event-service";
import { ParticipantService } from "@/lib/services/participant-service";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

/**
 * Displays an administrator-only check-in handler interface.
 * 
 * Presents a dropdown for choosing the active event to manage, and then a list of
 * participants who can check in for that event.
 */
export default function CheckinIndex() {
    // Top-of-page data
    const [events, setEvents] = useState<EventSummary[]>([]);
    const [selectedEvent, setSelectedEvent] = useState('-1');
    const [filter, setFilter] = useState('');

    // Main content data
    const [allParticipants, setAllParticipants] = useState<CheckinInfo[]>([]);
    const [displayedParticipants, setDisplayedParticipants] = useState<CheckinInfo[]>([]);

    // Utility function, loads additional data and appends to participant list.
    const setWithAdditionalData = (status: CheckinStatus) => {
        ParticipantService.getParticipantByEmail(status.email)
            .then(info => {
                const current = {
                    ...status,
                    firstName: info.firstName,
                    lastName: info.lastName
                };
                setAllParticipants(list => [...list, current].sort((a, b) => a.email.localeCompare(b.email)))
            });
    };
    
    // Load dropdown list on page load.
    useEffect(() => {
        EventService.search('')
            .then(data => setEvents(data))
            .catch(() => toast.error('Hiba történt: Az eseménylista betöltése nem sikerült!'));
    }, []);

    // Load all participants whenever the selected event changes.
    useEffect(() => {
        const eventId = parseInt(selectedEvent)
        if (eventId === -1) return;

        CheckinService.getForEvent(eventId)
            .then(data => {
                setAllParticipants([]);
                data.forEach(status => setWithAdditionalData(status));
            })
            .catch(() => toast.error('Hiba történt: A résztvevők lekérdezése nem sikerült!'));
    }, [selectedEvent]);

    // Frontend filtering for participants.
    useEffect(() => {
        setDisplayedParticipants(allParticipants.filter(p =>
            p.firstName.includes(filter) || p.lastName.includes(filter) || p.email.includes(filter)))
    }, [filter, allParticipants]);

    // Handle WebSocket connections.
    useEffect(() => {
        if (parseInt(selectedEvent) === -1) return;

        CheckinService.connectWS((newStatus) => {
            ParticipantService.getParticipantByEmail(newStatus.email)
                .then(info => {
                    const current = {
                        ...newStatus,
                        firstName: info.firstName,
                        lastName: info.lastName
                    };
                    setAllParticipants(list =>
                        [
                            ...list.map(e => (e.eventId == current.eventId && e.email == current.email) ? current : e)
                        ].sort((a, b) => a.email.localeCompare(b.email))
                    );
                });
        });

        return () => {
            CheckinService.disconnectWS();
        };
    }, [selectedEvent]);

    // Called when a status change is requested.
    const handleStatusChange = (targetStatus: CheckinStatus) => {
        CheckinService.sendStatusChangeWS(targetStatus);
    };

    // Render page content.
    return <>
        <AuthenticatedFrame title='Kapu'>
            <div className='flex-1 container mx-auto px-4'>
                <div className='flex flex-row my-4'>
                    <div className='w-1/2 mr-2'>
                        <select
                            value={selectedEvent} onChange={e => setSelectedEvent(e.target.value)}
                            className='block w-full px-2 py-3 text-lg border border-theme-300 bg-theme-50 text-theme-800'
                        >
                            <option value={-1}>Válassz eseményt!</option>
                            {
                                events.map((value, i) => <option key={i} value={value.id}>{value.title}</option>)
                            }
                        </select>
                    </div>
                    <div className='w-1/2 ml-2'>
                        <Input
                            className='w-full' placeholder='Résztvevők szűrése...'
                            values={[filter, setFilter]}
                        />  
                    </div>
                </div>

                {
                    displayedParticipants.map((p, i) => <CheckinCard key={i} checkin={p} onClick={handleStatusChange} />)
                }
            </div>
        </AuthenticatedFrame>
    </>;
}
