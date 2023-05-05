interface ComponentProps {
    /** The react state variable to receive state from, and the state setter function to set state with. */
    values: Array<any>,
    /** The type of the input. Defaults to 'text'. */
    type?: string,
    /** The id of the input. */
    id?: string,
    /** Specify additional styles here. */
    className?: string,
    /** Focus attribute of the input. */
    autoFocus?: boolean,
    /** Specify placeholder text here. */
    placeholder?: string,
    /** How to handle 'Enter' keypresses while typing. */
    onEnter?: () => void
};

/**
 * A pre-styled input component to reduce style duplication in the code base.
 * Width is not specified by default, but can be specified as additional classes.
 * 
 * Use the prop 'primary' to highlight the button as a primary button.
 */
export default function Input(props: ComponentProps) {
    const styles = 'block w-full p-2 text-lg border border-theme-300 placeholder:italic placeholder:text-theme-400 ';
        + (props.className ? props.className : '');

    return <input
        className={styles} type={props.type} id={props.id}
        autoFocus={props.autoFocus} placeholder={props.placeholder}
        value={props.values[0]} onChange={e => props.values[1](e.target.value)}
        onKeyDown={e => (e.key === 'Enter') && props.onEnter?.()}
    />;
};
