import Link from "next/link";

interface ComponentProps {
    /** Whether the button is a primary button or not. Results in different styles. */
    primary?: boolean,
    /** Specify additional styles here. */
    className?: string,
    /** HTML elements that should be wrapped. Generally a line of text. */
    children?: React.ReactNode,
    /** Whether the button is disabled or not. */
    disabled?: boolean,
    /** Where the link should point to. */
    href: string,
};

/**
 * A pre-styled link-button component to reduce style duplication in the code base.
 * Margins are not added by default, but can be specified as additional classes.
 * 
 * Use the prop 'primary' to highlight the button as a primary button.
 * Opens links in a new tab.
 */
export default function LinkButton(props: ComponentProps) {
    const styles = 'text-lg px-8 py-3 text-center '
        + (props.disabled ? '' : 'hover:outline hover:outline-3 hover:outline-theme-800 ')
        + (props.primary ? 'text-theme-100 bg-theme-800 ' : 'text-theme-800 ')
        + (props.className ? props.className : '');

    return <a href={props.href} className={styles}>
        {props.children}
    </a>;
};
