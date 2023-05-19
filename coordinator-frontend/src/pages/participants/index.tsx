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

    const load = () => {
        ParticipantService.getAllParticipants()
            .then(data => setParticipants(data))
            .catch(() => toast.error('Hiba történt: A résztvevők betöltése nem sikerült!'));
    };

    useEffect(() => load(), []);

    const handleDeleteClick = (id: string) => {
        ParticipantService.remove(id)
            .then(() => {
                load();
                toast.success('Résztvevő sikeresen törölve!');
            })
            .catch(() => toast.error('Hiba történt: A résztvevő törlése nem sikerült!'));
    };

    return <>
        <AuthenticatedFrame title='Regisztrált résztvevők'>
            <div className='flex-1 container mx-auto px-4'>
                <h1 className='mt-6 mb-4 text-2xl text-theme-800'>Regisztrált résztvevők</h1>

                <table className='table-fixed w-full border-collapse'>
                    <thead>
                        <tr>
                            {
                                ['Vezetéknév', 'Keresztnév', 'Email', 'Lakcím',
                                 'Telefonszám', 'Megjegyzések', 'Műveletek']
                                    .map((value, i) => 
                                        <th
                                            className='px-4 py-3 bg-theme-800 text-theme-50 text-lg'
                                            key={i} scope='col'
                                        >
                                            {value}
                                        </th>
                                    )
                            }
                        </tr>
                    </thead>
                    <tbody>
                        {
                            participants.map((value, i) =>
                                <tr className='border-b border-theme-600 hover:bg-theme-100' key={i}>
                                    <td className='text-theme-800 p-4'>{value.lastName}</td>
                                    <td className='text-theme-800 p-4'>{value.firstName}</td>
                                    <td className='text-theme-800 p-4'>{value.email}</td>
                                    <td className='text-theme-800 p-4'>{value.address}</td>
                                    <td className='text-theme-800 p-4'>{value.phoneNumber}</td>
                                    <td className='text-theme-800 p-4'>{value.notes}</td>
                                    <td>
                                            <button
                                                className='p-2 hover:outline hover:outline-3 hover:outline-theme-800 text-theme-800'
                                                onClick={() => handleDeleteClick(value.id.toString())}
                                            >
                                                Törlés
                                            </button>
                                    </td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
            </div>
        </AuthenticatedFrame>
    </>;
}
