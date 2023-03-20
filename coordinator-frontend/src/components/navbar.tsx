import Link from 'next/link';
import { useRouter } from 'next/router';

const navigationItems = [
    { name: 'Hírek', path: '/news' },
    { name: 'Új bejegyzés', path: '/news/create' }
];

export default function Navbar() {
    const activeClasses = 'text-gray-100 bg-gray-700 ';
    const inactiveClasses = 'text-gray-700 ';
    const commonClasses = 'hover:outline hover:outline-3 hover:outline-gray-700 px-6 py-3 mx-2 text-lg ';

    const { asPath } = useRouter()

    const navElements = navigationItems.map((item, i) => {
        const classes = (asPath === item.path) ? (commonClasses + activeClasses) : (commonClasses + inactiveClasses);
        return <Link href={item.path} key={i} className={classes}>{item.name}</Link>
    });

    return (
        <header>
            <nav className='flex items-center px-4 py-5 bg-gray-100'>
                <div className='text-gray-700 text-lg ml-2 mr-6'>
                    <span>Event Coordinator</span>
                </div>
                <div className='gap-x-12 mr-auto'>
                    {navElements}
                </div>
                <div>
                    <Link href='/' className={commonClasses + inactiveClasses}>Bejelentkezés</Link>
                </div>
            </nav>
        </header>
    );
}
