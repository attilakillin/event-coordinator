import DOMPurify from "dompurify";
import dynamic from "next/dynamic";

interface ComponentProps {
    /** HTMl content to sanitize, format, and display inside this component. */
    content: string
};

/**
 * A component that can accept a HTML content output by QuillJS, and format it appropriately.
 * Displays the final result inside this component.
 */
function QuillRenderer(props: ComponentProps) {
    let dom = new DOMParser().parseFromString(props.content, 'text/html').body;

    // The heading tags had their styles removed while normalizing, and need to be restyled.
    dom.querySelectorAll('h1').forEach(e => e.classList.add('text-2xl'));
    dom.querySelectorAll('h2').forEach(e => e.classList.add('text-xl'));

    // The font families have to be set manually as well.
    dom.querySelectorAll('.ql-font-sans').forEach(e => e.classList.add('font-sans'));
    dom.querySelectorAll('.ql-font-serif').forEach(e => e.classList.add('font-serif'));
    dom.querySelectorAll('.ql-font-monospace').forEach(e => e.classList.add('font-mono'));
    
    // Bold, italic, underline, and the color utilities work without changes.

    // Links have to be noticeable.
    dom.querySelectorAll('a').forEach(e => e.classList.add('underline', 'text-theme-600'));

    // Lists can be styled by modifying their root element.
    dom.querySelectorAll('ol').forEach(e => e.classList.add('list-decimal', 'pl-8'));
    dom.querySelectorAll('ul').forEach(e => e.classList.add('list-disc', 'pl-8'));

    // Blockquotes need a bunch of additional classes.
    dom.querySelectorAll('blockquote').forEach(e => {
        e.classList.add('border-theme-800', 'border-l-4', 'pl-3', 'italic');
    });

    // And lastly, indentations are a bit tricky.
    dom.querySelectorAll<HTMLElement>('[class*="ql-indent-"').forEach(e => {
        const depth = Array.from(e.classList).find(c => c.startsWith('ql-indent-'))?.replace('ql-indent-', '') ?? '0';
        e.style.marginLeft = (parseInt(depth) * 40) + 'px';
    });

    // Purify resulting HTML just in case (although the server should already have done so).
    const result = DOMPurify.sanitize(dom.innerHTML);
    
    return <div className='text-theme-800' dangerouslySetInnerHTML={{ __html: result }}></div>;
};

// As the DOMPurify component only exists in the browser, server-side rendering must be disabled.
export default dynamic(() => Promise.resolve(QuillRenderer), { ssr: false });
