import CenteredFrame from "@/components/frames/centered-frame";
import { AuthenticationService } from "@/lib/services/auth-service";
import { useRouter } from "next/router";
import { useEffect } from "react";
import { toast } from "react-toastify";

/**
 * Logs the user out, and redirects them home.
 */
export default function AdminLogout() {
    const router = useRouter();

    useEffect(() => {
        AuthenticationService.logout().then(() => {
            toast.success('Sikeres kijelentkezés!');
            router.push('/');
        });
    }, [router]);

    // Temporary page while logging out is in progress.
    return <CenteredFrame title='Kijelentkezés'>Kijelentkezés folyamatban...</CenteredFrame>;
};
