import Navbar from "../blocks/navbar";
import AppHead from "../builtin/app-head";

interface ComponentProps {
    /** The title of the page. The domain name is automatically appended. */
    title: string,
    /** HTML elements that should be wrapped. */
    children?: React.ReactNode
}

/**
 * A simple page frame that displays its children with a navigation bar and a title.
 */
export default function BasicFrame(props: ComponentProps) {
    return <>
        <AppHead title={`${props.title} - Esemény koordinátor`} />
        <Navbar />

        {props.children}
    </>;
}
