import Button from "@/components/builtin/button";
import { CheckinInfo } from "@/components/types/checkininfo";
import { CheckinStatus } from "@/components/types/checkinstatus";

interface ComponentProps {
    /** Check-in info for a specific participant. */
    checkin: CheckinInfo,
    onClick: (targetStatus: CheckinStatus) => void
};

/** 
 * A card component displaying info about a participant's check-in status.
 */
export default function CheckinCard(props: ComponentProps) {

    const buttonMaker = (which: string, display: string) => {
        return <Button
            className='mr-4 text-2xl' primary={props.checkin.status == which}
            onClick={() => props.onClick({
                eventId: props.checkin.eventId,
                email: props.checkin.email,
                status: which
            })}
        >
            {display}
        </Button>;
    }

    // Return rendered check-in card.
    return (
        <div className='bg-theme-100 px-4 pt-3 pb-3 mb-3 text-theme-800'>
            <div className='flex flex-col md:flex-row my-4'>
                <div className='w-full md:w-3/5 mr-2'>
                    <h1 className='text-2xl'>{props.checkin.lastName} {props.checkin.firstName}</h1>
                    <p>{props.checkin.email}</p>
                </div>
                <div className='w-full md:w-2/5 ml-2 mt-2 md:mt-0 flex flex-row justify-center md:justify-end'>
                    { buttonMaker('DECLINED', '-') }
                    { buttonMaker('UNKNOWN', '?') }
                    { buttonMaker('CHECKED_IN', '+') }
                </div>
            </div>
        </div>
    );
};
