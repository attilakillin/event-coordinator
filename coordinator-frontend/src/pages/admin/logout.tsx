import Navbar from "@/components/blocks/navbar";
import AppHead from "@/components/builtin/app-head";
import { AuthenticationService } from "@/lib/services/auth-service";
import { useRouter } from "next/router";
import { useEffect } from "react";

/**
 * Logs the user out, and redirects them home.
 */
export default function AdminLogout() {
    const router = useRouter();

    useEffect(() => {
        AuthenticationService.logout().then(() => router.push('/'));
    }, [router]);

    // Temporary page while logging out is in progress.
    return <>
        <AppHead title='Kijelentkezés - Esemény koordinátor' />
        <Navbar />

        <div className='flex-1 mx-auto container flex items-center justify-center'>
            <div>Kijelentkezés folyamatban...</div>
        </div>
    </>;
};
