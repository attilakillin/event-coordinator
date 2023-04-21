import { AuthService } from "@/services/auth-service";
import { useRouter } from "next/router";
import { useEffect } from "react";

export default function AdminLogout() {
    const router = useRouter();
    useEffect(() => {
        AuthService.logout()
            .then(_ => router.push('/'));
    }, []);

    return <div></div>;
}
