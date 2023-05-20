import { MouseEventHandler } from "react";

interface ComponentProps {
    /** Whether the button is a primary button or not. Results in different styles. */
    primary?: boolean,
    /** Specify additional styles here. */
    className?: string,
    /** Click handler for the button. The callback will be delegated to the wrapped HTML button element. */
    onClick?: MouseEventHandler<HTMLButtonElement>,
    /** HTML elements that should be wrapped. Generally a line of text. */
    children?: React.ReactNode,
    /** Whether the button is disabled or not. */
    disabled?: boolean
};

/**
 * A pre-styled button component to reduce style duplication in the code base.
 * Margins are not added by default, but can be specified as additional classes.
 * 
 * Use the prop 'primary' to highlight the button as a primary button.
 */
export default function Button(props: ComponentProps) {
    const styles = 'text-lg px-8 py-3 '
        + (props.disabled ? '' : 'hover:outline hover:outline-3 hover:outline-theme-800 ')
        + (props.primary ? 'text-theme-100 bg-theme-800 ' : 'text-theme-800 ')
        + (props.className ? props.className : '');

    return <button className={styles} onClick={props.onClick} disabled={props.disabled}>{props.children}</button>;
};
