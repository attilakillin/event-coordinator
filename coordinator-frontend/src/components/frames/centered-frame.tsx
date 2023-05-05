import Navbar from "../blocks/navbar";
import AppHead from "../builtin/app-head";

interface ComponentProps {
    /** The title of the page. The domain name is automatically appended. */
    title: string,
    /** HTML elements that should be wrapped. */
    children?: React.ReactNode
}

/**
 * A page frame that displays its children in the center of the page.
 */
export default function CenteredFrame(props: ComponentProps) {
    return <>
        <AppHead title={`${props.title} - Esemény koordinátor`} />
        <Navbar />

        <div className='flex-1 mx-auto container flex items-center justify-center'>
            {props.children}
        </div>
    </>;
}
