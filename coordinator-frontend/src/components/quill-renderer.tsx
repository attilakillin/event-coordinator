
/* Custom prop type accepting the Quill Delta type. */
interface ComponentProps {
    delta: {
        ops?: Array<{
            attributes?: any,
            insert: string
        }>
    };
}

/* A component that can accept a Delta output by QuillJS and formats it adequately for displaying.
 * By default, Tailwind removes formatting for all basic HTML tags, which makes Deltas a better choice here. */
export default function QuillRenderer(props: ComponentProps) {
    return (
        <div>{JSON.stringify(props.delta.ops)}</div>
    );
}
