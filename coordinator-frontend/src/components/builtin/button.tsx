import { MouseEventHandler } from "react";

interface Props {
    primary?: boolean,
    onClick?: MouseEventHandler<HTMLButtonElement>,
    className?: string,
    children?: React.ReactNode
};

/**
 * A pre-styled button component to reduce style duplication in the code base.
 * Margins are not added by default, but can be specified as additional classes.
 */
export default function Button(props: Props) {
    /* Conditionally set styles. */
    let styles = 'hover:outline hover:outline-3 hover:outline-stone-800 px-8 py-3 text-lg ';
    styles += (props.primary) ? 'text-stone-100 bg-stone-800 ' : 'text-stone-800 ';
    styles += (props.className) ? props.className : '';

    return <button className={styles} onClick={props.onClick}>{props.children}</button>;
}
