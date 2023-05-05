
import { AuthenticationStatus, useAuthentication } from "@/lib/hooks/authentication";
import Navbar from "../blocks/navbar";
import AppHead from "../builtin/app-head";
import { useRouter } from "next/router";
import { toast } from "react-toastify";
import CenteredFrame from "./centered-frame";

interface ComponentProps {
    /** The title of the page. The domain name is automatically appended. */
    title: string,
    /** HTML elements that should be wrapped. */
    children?: React.ReactNode
}

/**
 * A page frame that requires authentication to display its content.
 * Redirects the user to the root path upon unsuccessful authentication.
 */
export default function AuthenticatedFrame(props: ComponentProps) {
    // Require authentication for conditional rendering.
    const status = useAuthentication();
    const router = useRouter();

    // While pending, return a loading text.
    if (status == AuthenticationStatus.PENDING) {
        return <CenteredFrame title={props.title}>Betöltés...</CenteredFrame>
    }

    // Upon failure, redirect the user home.
    if (status == AuthenticationStatus.FAILURE) {
        toast.error(<div>Sikertelen authentikáció!<br/>Jelentkezzen be újra!</div>);
        router.push('/');
        return <></>;
    }

    // Else return the main content.
    return <>
        <AppHead title={`${props.title} - Esemény koordinátor`} />
        <Navbar />

        {props.children}
    </>;
}
