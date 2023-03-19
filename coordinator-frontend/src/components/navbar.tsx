import Link from 'next/link'

export default function Navbar() {
    const navItemClasses = 'text-gray-700 hover:outline hover:outline-3 hover:outline-gray-700 px-6 py-3 text-lg';

    return (
        <header>
            <nav className='flex items-center px-4 py-5 bg-gray-100'>
                <div className='text-gray-700 text-lg ml-2 mr-6'>
                    <span>Event Coordinator</span>
                </div>
                <div className='gap-x-6 mr-auto'>
                    <Link href='/' className={navItemClasses}>Hírek</Link>
                </div>
                <div>
                    <Link href='/' className={navItemClasses}>Bejelentkezés</Link>
                </div>
            </nav>
        </header>
    )
}
