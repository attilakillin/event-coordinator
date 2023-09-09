import { AuthenticationStatus, useAuthentication } from '@/lib/hooks/authentication';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useState } from 'react';

/** Typescript type for a simple navigation link. */
interface SimpleLink {
    name: string,
    path: string
};

/** Typescript type for a complex, dropdown navigation link group. */
interface DropdownLink {
    name: string,
    children: SimpleLink[]
};

/**
 * Populates the navigation item list for unathenticated users.
 */
const basePaths: SimpleLink[] = [
    { name: 'Hírek', path: '/articles' },
    { name: 'Jelentkezés', path: '/participants/register' },
    { name: 'Események', path: '/events' }
];

/**
 * Populates the navigation item list for authenticated users.
 */
const authPaths: DropdownLink[] = [
    {
        name: 'Hírportál',
        children: [
            { name: 'Hírek', path: '/articles' },
            { name: 'Piszkozatok', path: '/articles/drafts' },
            { name: 'Új bejegyzés', path: '/articles/edit' }
        ]
    },
    {
        name: 'Résztvevők',
        children: [
            { name: 'Résztvevőlista', path: '/participants' },
            { name: 'Jelentkezés', path: '/participants/register' }
        ]
    },
    {
        name: 'Események',
        children: [
            { name: 'Eseménylista', path: '/events' }
        ]
    }
];

/**
 * A simple clickable navigation item link.
 */
function SimpleNavItem(props: { item: SimpleLink, location?: string, key?: number }) {
    const styles = 'px-6 py-3 mx-3 my-1 whitespace-nowrap hover:outline hover:outline-3 hover:outline-theme-800 '
        + ((props.item.path === props.location) ? 'bg-theme-800 text-theme-100' : '');

    return (
        <Link key={props.key} href={props.item.path} className={styles}>{props.item.name}</Link>
    );
}

/**
 * A complex navigation item that functions as a dropdown.
 * Its children are clickable navigation links.
 */
function DropdownNavItem(props: { item: DropdownLink, location?: string, key?: number }) {
    const rootStyle = 'group relative';
    const itemStyle = 'px-6 py-3 mx-2 my-1 hover:outline hover:outline-3 hover:outline-theme-800 '
        + ((props.item.children.some(child => (child.path === props.location))) ? 'bg-theme-800 text-theme-100' : '');
    const listStyle = 'flex flex-wrap flex-col bg-theme-100 py-1 ml-6 '
        + 'md:hidden md:group-hover:flex md:flex-nowrap md:w-auto md:group-hover:absolute '
        + 'md:ml-0 md:drop-shadow-lg md:pb-2';

    return (
        <div key={props.key} className={rootStyle}>
            <div className={itemStyle}>{props.item.name}</div>

            <div className={listStyle}>
                {
                    props.item.children.map((child, i) =>
                        <SimpleNavItem key={i} location={props.location} item={child} />
                    )
                }
            </div>
        </div>
    );
}

/**
 * A component containing the whole site-wide navigation bar present on the top of a page.
 */
export default function Navbar() {
    // Require authentication for conditional rendering.
    const status = useAuthentication();
    const { asPath } = useRouter();

    // Handle mobile navigation bar expansion
    const [expanded, setExpanded] = useState(false);
    const handleExpandClick = () => setExpanded(val => !val);

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
                        ? authPaths.map((child, i) => <DropdownNavItem item={child} location={asPath} key={i} />)
                        : basePaths.map((child, i) => <SimpleNavItem item={child} location={asPath} key={i} />)
                    }

                    <div className='flex-1'></div>

                    {
                        (status == AuthenticationStatus.SUCCESS)
                        ? <SimpleNavItem item={({ name: 'Kijelentkezés', path: '/admin/logout' })} location={asPath} />
                        : <SimpleNavItem item={({ name: 'Bejelentkezés', path: '/admin/login' })} location={asPath} />
                    }
                </div>
            </nav>
        </header>
    );
};
