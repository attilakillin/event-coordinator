import { AuthenticationStatus, useAuthentication } from '@/lib/hooks/authentication';
import Link from 'next/link';
import { useRouter } from 'next/router';

/**
 * Populates the navigation item list. These elements are visible to all users.
 */
const basePathList = [
    { name: 'Hírek', path: '/articles' },
];

/**
 * Populates the navigation item list. These elements are only visible to authenticated users.
 */
const authPathList = [
    { name: 'Piszkozatok', path: '/articles/drafts' },
    { name: 'Új bejegyzés', path: '/articles/edit' }
];

/**
 * A component containing the whole site-wide navigation bar present on the top of a page.
 */
export default function Navbar() {
    // Require authentication for conditional rendering.
    const status = useAuthentication();
    const { asPath } = useRouter();

    // Tailwind styles and conditional styling functions.
    const inactiveStyles = 'px-6 py-3 mx-2 hover:outline hover:outline-3 hover:outline-theme-800';
    const selectedStyles = inactiveStyles + ' text-theme-100 bg-theme-800';
    const addStyles = (path: string) => {
        return (asPath == path) ? selectedStyles : inactiveStyles;
    };

    // Apply different classes to nav items based on current path.
    const mapLinks = (links: Array<any>) => {
        return links.map((item, i) =>
            <Link key={i} href={item.path} className={addStyles(item.path)}>{item.name}</Link>
        );
    };

    // Generate page header.
    return (
        <header>
            <nav className='flex items-center text-theme-800 bg-theme-100 text-lg px-4 py-5'>
                <div className='ml-2 mr-6'>
                    <Link href='/'>Esemény koordinátor</Link>
                </div>
                <div className='mr-auto'>
                    {
                        (status == AuthenticationStatus.SUCCESS)
                        ? mapLinks(basePathList.concat(authPathList))
                        : mapLinks(basePathList)
                    }
                </div>
                <div>
                    {
                        (status == AuthenticationStatus.SUCCESS)
                        ? <Link href='/admin/logout' className={inactiveStyles}>
                            Kijelentkezés
                        </Link>
                        : <Link href='/admin/login' className={addStyles('/admin/login')}>
                            Bejelentkezés
                        </Link>
                    }
                </div>
            </nav>
        </header>
    );
};
