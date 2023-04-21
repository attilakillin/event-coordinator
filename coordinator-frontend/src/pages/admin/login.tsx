import AppHead from "@/components/builtin/app_head";
import Button from "@/components/builtin/button";
import Navbar from "@/components/navbar";
import { AuthService } from "@/services/auth-service";
import Link from "next/link";
import { useState } from "react";

export default function AdminLogin() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleLoginClick = () => {
        AuthService.login(username, password)
            .then(data => console.log(data))
    }

    return (
        <>
            <AppHead title='Bejelentkezés - Coordinator' />
            <Navbar />

            <div className='flex-1 mx-auto container flex items-center justify-center'>
                <div className='bg-stone-100 p-8'>
                    <div className='mb-4'>
                        <label className='block text-stone-800 mb-1'>Felhasználónév</label>
                        <input className='block w-full text-lg placeholder:italic placeholder:text-stone-400 border border-stone-300 px-2 py-2'
                               type='text' value={username} onChange={e => setUsername(e.target.value)}/>
                    </div>
                    <div className='mb-6'>
                        <label className='block text-stone-800 mb-1'>Jelszó</label>
                        <input className='block w-full text-lg placeholder:italic placeholder:text-stone-400 border border-stone-300 px-2 py-2'
                               type='password' value={password} onChange={e => setPassword(e.target.value)} />
                    </div>
                    <div className='flex items-center justify-between'>
                        <Button primary onClick={handleLoginClick}>Bejelentkezés</Button>
                        <Link href='/admin/register' className='text-stone-800'>Regisztráció</Link>
                    </div>
                </div>
            </div>
        </>
    );
}
