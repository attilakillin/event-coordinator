import Link from 'next/link';
import { useRouter } from 'next/router';

/* These will be used to populate the left-aligned navigation bar item list. */
const navPathList = [
    { name: 'Hírek', path: '/news' },
    { name: 'Új bejegyzés', path: '/news/create' }
];

/* A component containing the whole page-wide navigation bar present on the top of a page. */
export default function Navbar() {
    /* Tailwind styling classes. */
    const selected = 'hover:outline hover:outline-3 hover:outline-stone-800 px-6 py-3 mx-2 text-stone-100 bg-stone-800';
    const inactive = 'hover:outline hover:outline-3 hover:outline-stone-800 px-6 py-3 mx-2';

    /* Apply different classes to nav items based on current path. */
    const { asPath } = useRouter();
    const links = navPathList.map((item, i) =>
        <Link href={item.path} key={i} className={(asPath === item.path) ? selected : inactive}>{item.name}</Link>
    );

    /* Generate header markup. */
    return (
        <header>
            <nav className='flex items-center text-stone-800 bg-stone-100 text-lg px-4 py-5'>
                <div className='ml-2 mr-6'>
                    <span>Event Coordinator</span>
                </div>
                <div className='mr-auto'>
                    {links}
                </div>
                <div>
                    <Link href='/' className={inactive}>Bejelentkezés</Link>
                </div>
            </nav>
        </header>
    );
}
