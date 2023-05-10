import AppHead from "../builtin/app-head";

interface ComponentProps {
    /** The title of the page. The domain name is automatically appended. */
    title: string,
    /** HTML elements that should be wrapped. */
    children?: React.ReactNode
}

/**
 * A page frame that displays its children in the center of the page, without the navigation bar
 * or any other extra components. Use this to display the children without any additional side
 * effects (such as the navigation bar running an authentication check).
 */
export default function EmptyFrame(props: ComponentProps) {
    return <>
        <AppHead title={`${props.title} - Esemény koordinátor`} />

        <div className='flex-1 mx-auto container flex items-center justify-center'>
            {props.children}
        </div>
    </>;
}
