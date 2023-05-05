import Head from "next/head";

interface ComponentProps {
    /** The tab title to display in the browser. */
    title: string
};

/**
 * Groups common content loaded in the HTML header together.
 * Accepts a custom title to display in the browser.
 */
export default function AppHead(props: ComponentProps) {
    return (
        <Head>
            <title>{props.title}</title>
            <meta name='viewport' content='width=device-width, initial-scale=1' />
        </Head>
    );
};
