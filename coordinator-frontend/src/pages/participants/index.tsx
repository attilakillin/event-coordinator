import AuthenticatedFrame from "@/components/frames/authenticated-frame";

/**
 * Displays the list of registered participants. The viewers of this page
 * can also delete any one participant.
 * 
 * Requires authentication.
 */
export default function ParticipantIndex() {
    return <>
        <AuthenticatedFrame title='Regisztrált résztvevők'>

        </AuthenticatedFrame>
    </>;
}
