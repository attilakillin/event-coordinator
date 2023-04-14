import DOMPurify from "dompurify";
import dynamic from "next/dynamic";

/* Custom prop type accepting a HTML inner string. */
interface ComponentProps {
    content: string
}

/* A component that can accept a HTML content output by QuillJS, and formats it appropriately. */
function QuillRenderer(props: ComponentProps) {
    let dom = new DOMParser().parseFromString(props.content, 'text/html').body;

    /* The heading tags have their styles removed, and need to be reformatted. */
    dom.querySelectorAll('h1').forEach(e => e.classList.add('text-2xl'));
    dom.querySelectorAll('h2').forEach(e => e.classList.add('text-xl'));

    /* The font families have to be set manually as well. */
    dom.querySelectorAll('.ql-font-sans').forEach(e => e.classList.add('font-sans'));
    dom.querySelectorAll('.ql-font-serif').forEach(e => e.classList.add('font-serif'));
    dom.querySelectorAll('.ql-font-monospace').forEach(e => e.classList.add('font-mono'));
    
    /* Bold, italic, underline, and the color utilites work without changes. */

    /* Links have to be styled. */
    dom.querySelectorAll('a').forEach(e => e.classList.add('underline', 'text-stone-600'));

    /* Images work out-of-the-box, but we add a bit of padding, just for show. */
    dom.querySelectorAll('img').forEach(e => e.classList.add('px-2'));

    /* Lists just need one single class on their root element. */
    dom.querySelectorAll('ol').forEach(e => e.classList.add('list-decimal', 'pl-8'));
    dom.querySelectorAll('ul').forEach(e => e.classList.add('list-disc', 'pl-8'));

    /* Blockquotes need a bunch of additional classes. */
    dom.querySelectorAll('blockquote').forEach(e =>
        e.classList.add('border-stone-800', 'border-l-4', 'pl-3', 'italic'));

    /* And lastly, indentations. */
    dom.querySelectorAll<HTMLElement>('[class*="ql-indent-"').forEach(e => {
        const depth = Array.from(e.classList)
            .find(c => c.startsWith('ql-indent-'))
            ?.replace('ql-indent-', '') ?? '0';

        const indent = parseInt(depth) * 40;
        e.style.marginLeft = indent + 'px';
    });

    /* Purify resulting HTML (prevent attacks from malicious content). */
    const result = DOMPurify.sanitize(dom.innerHTML);
    
    return (
        <div className='text-stone-800' dangerouslySetInnerHTML={{ __html: result }}></div>
    );
}

export default dynamic(() => Promise.resolve(QuillRenderer), { ssr: false });
