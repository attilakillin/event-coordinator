import { AuthService } from '@/services/auth-service';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

/* These will be used to populate the left-aligned navigation bar item list.
 * The latter one is only visible when the user is authenticated. */
const basePathList = [
    { name: 'Hírek', path: '/articles' },
];

const authPathList = [
    { name: 'Új bejegyzés', path: '/articles/edit' }
];

/* A component containing the whole page-wide navigation bar present on the top of a page. */
export default function Navbar() {
    /* Tailwind styling classes. */
    const selected = 'hover:outline hover:outline-3 hover:outline-stone-800 px-6 py-3 mx-2 text-stone-100 bg-stone-800';
    const inactive = 'hover:outline hover:outline-3 hover:outline-stone-800 px-6 py-3 mx-2';

    const [loggedIn, setLoggedIn] = useState(false);
    useEffect(() => {
        AuthService.validate()
            .then(_ => setLoggedIn(true))
            .catch(_ => setLoggedIn(false));
    }, []);

    /* Apply different classes to nav items based on current path. */
    const { asPath } = useRouter();
    const mapLinks = (links: Array<any>) => links.map((item, i) =>
        <Link href={item.path} key={i} className={(asPath === item.path) ? selected : inactive}>{item.name}</Link>
    );

    /* Generate header markup. */
    return (
        <header>
            <nav className='flex items-center text-stone-800 bg-stone-100 text-lg px-4 py-5'>
                <div className='ml-2 mr-6'>
                    <Link href='/'>Event Coordinator</Link>
                </div>
                <div className='mr-auto'>
                    {(loggedIn) ? mapLinks(basePathList.concat(authPathList)) : mapLinks(basePathList)}
                </div>
                <div>
                    {(loggedIn)
                    ?   <Link href='/admin/logout' className={inactive}>
                            Kijelentkezés
                        </Link>
                    :   <Link href='/admin/login' className={(asPath === '/admin/login') ? selected : inactive}>
                            Bejelentkezés
                        </Link>
                    }
                </div>
            </nav>
        </header>
    );
}
