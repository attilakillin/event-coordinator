import { AuthenticationStatus, useAuthentication } from '@/lib/hooks/authentication';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useState } from 'react';

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
    const inactiveStyles = 'px-6 py-3 mx-2 my-1 hover:outline hover:outline-3 hover:outline-theme-800';
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

    // Handle mobile navigation bar expansion
    const [expanded, setExpanded] = useState(false);

    const handleExpandClick = () => {
        setExpanded(val => !val);
    }

    // Generate page header.
    return (
        <header>
            <nav className='flex flex-wrap items-center text-theme-800 bg-theme-100 text-lg px-4 py-3 md:py-2'>
                <div className='ml-2 mr-6'>
                    <Link href='/'>Esemény koordinátor</Link>
                </div>

                <div className='ml-auto md:hidden'>
                    <button
                        onClick={handleExpandClick}
                        className='p-2 hover:outline hover:outline-3 hover:outline-theme-600'
                    >
                        <svg className='fill-theme-600' viewBox="0 0 32 32" width="32" height="32">
                            <rect y="4" width="32" height="5"/>
                            <rect y="14" width="32" height="5"/>
                            <rect y="24" width="32" height="5"/>
                        </svg>
                    </button>
                </div>

                <div className={
                    (expanded)
                        ? 'flex flex-col flex-wrap w-full mt-2 md:flex-row md:flex-1 md:mt-0'
                        : 'hidden md:flex md:flex-1'
                }>
                    {
                        (status == AuthenticationStatus.SUCCESS)
                        ? mapLinks(basePathList.concat(authPathList))
                        : mapLinks(basePathList)
                    }

                    <div className='flex-1'></div>

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
