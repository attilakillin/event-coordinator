import AppHead from "@/components/builtin/app-head";
import Button from "@/components/builtin/button";
import Navbar from "@/components/blocks/navbar";
import { AuthenticationService } from "@/lib/services/auth-service";
import { useRouter } from "next/router";
import { useState } from "react";
import { toast } from "react-toastify";
import Input from "@/components/builtin/input";

/**
 * Display the administrator login screen. The user can authenticate themselves.
 * Upon success, redirect the user home.
 */
export default function AdminLogin() {
    // Form element values.
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const router = useRouter();

    // Tailwind styles.
    const labelStyles = 'block mb-1 text-theme-800';

    // Handle login button clicks.
    const handleLoginClick = () => {
        AuthenticationService.login(username, password)
            .then(() => {
                toast.success(<div>Sikeres bejelentkezés!</div>);
                router.push('/');
            })
            .catch(() => {
                toast.error(<div>Sikertelen bejelentkezés!<br/>A megadott azonosítók nem helyesek!</div>);
            });
    };

    // Generate rendered page.
    return <>
        <AppHead title='Bejelentkezés - Esemény koordinátor' />
        <Navbar />

        <div className='flex-1 mx-auto container flex items-center justify-center'>
            <div className='bg-theme-100 p-8'>
                <div className='mb-4'>
                    <label className={labelStyles} htmlFor='username'>Felhasználónév</label>
                    <Input 
                        className='w-full' id='username' values={[username, setUsername]} autoFocus
                        onEnter={() => document.getElementById('password')!.focus()}
                    />
                </div>
                <div className='mb-6'>
                    <label className={labelStyles} htmlFor='password'>Jelszó</label>
                    <Input 
                        className='w-full' type='password' id='password'
                        values={[password, setPassword]} onEnter={handleLoginClick}
                    />
                </div>
                <div className='flex justify-center'>
                    <Button primary onClick={handleLoginClick}>Bejelentkezés</Button>
                </div>
            </div>
        </div>
    </>;
};
