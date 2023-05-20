import ParticipantTable from "@/components/blocks/participants/participant-table";
import Button from "@/components/builtin/button";
import AuthenticatedFrame from "@/components/frames/authenticated-frame";
import Id from "@/components/types/id";
import { Participant } from "@/components/types/participant";
import { ParticipantService } from "@/lib/services/participant-service";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

/**
 * Displays the list of registered participants. The viewers of this page
 * can also delete any one participant.
 * 
 * Requires authentication.
 */
export default function ParticipantIndex() {
    // Manage list content state.
    const [participants, setParticipants] = useState<Array<Participant & Id>>([]);

    // Load participant list from backend.
    const loadParticipants = () => {
        ParticipantService.getAllParticipants()
            .then(data => setParticipants(data))
            .catch(() => toast.error('Hiba történt: A résztvevők betöltése nem sikerült!'));
    };

    // Always load participants on page load.
    useEffect(() => loadParticipants(), []);

    // Handle delete button click for a row.
    const handleDeleteClick = (id: string) => {
        ParticipantService.remove(id)
            .then(() => {
                loadParticipants();
                toast.success('Résztvevő sikeresen törölve!');
            })
            .catch(() => toast.error('Hiba történt: A résztvevő törlése nem sikerült!'));
    };

    // Handle export button click.
    const handleExportClick = () => {

    };

    // Render page content.
    return <>
        <AuthenticatedFrame title='Regisztrált résztvevők'>
            <div className='flex-1 container mx-auto px-4'>
                <h1 className='mt-6 mb-4 text-2xl text-theme-800'>Regisztrált résztvevők</h1>

                <ParticipantTable data={participants} />

                <div className='mt-4 flex flex-row justify-center'>
                    <Button onClick={handleExportClick}>Exportálás (XLS)</Button>
                </div>
            </div>
        </AuthenticatedFrame>
    </>;
}
