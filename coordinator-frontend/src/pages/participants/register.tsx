import Button from "@/components/builtin/button";
import Input from "@/components/builtin/input";
import CenteredFrame from "@/components/frames/centered-frame";
import { Participant } from "@/components/types/participant";
import { ParticipantService } from "@/lib/services/participant-service";
import { useRouter } from "next/router";
import { useState } from "react";
import { toast } from "react-toastify";

/**
 * Displays a registration form where future participants can enter their details.
 */
export default function ParticipantRegister() {
    // Form element values.
    const [fields, setFields] = useState<Participant>({
        lastName: '',
        firstName: '',
        email: '',
        address: '',
        phoneNumber: '',
        notes: ''
    });

    const setFieldSetter = (which: string) => {
        return (value: string) => setFields((prev) => {
            prev[which as keyof Participant] = value;
            return { ...prev };
        })
    };

    // Tailwind styles.
    const labelStyles = 'block mb-1 text-theme-800';

    const router = useRouter();

    // Handle submit button clicks.
    const handleSubmitClick = () => {
        if (!fields.lastName) { toast.error('A vezetéknév mező kötelező!'); return; }
        if (!fields.firstName) { toast.error('A keresztnév mező kötelező!'); return; }
        if (!fields.email.match(/.+@.+\..+/)) { toast.error('Az email mező kötelező!'); return; }

        ParticipantService.post(fields)
            .then(() => {
                toast.success('Sikeres regisztráció!');
                router.push('/');
            })
            .catch(() => {
                toast.error(<div>A regisztráció nem sikerült!</div>);
            });
    };

    // Render page content.
    return <>
        <CenteredFrame title='Regisztráció'>
            <div className='flex-1 container mx-auto px-4 flex flex-col'>
                <div className='bg-theme-100 p-8 my-2'>
                    <h1 className='mb-4 text-2xl text-theme-800'>Regisztráció</h1>

                    <div className='flex flex-col md:flex-row'>
                        <div className='mb-4 md:w-1/2 md:mr-2'>
                            <label className={labelStyles} htmlFor='lastName'>
                                Vezetéknév <span className='text-red-600'>*</span>
                            </label>
                            <Input 
                                id='lastName' values={[fields.lastName, setFieldSetter('lastName')]} autoFocus
                                onEnter={() => document.getElementById('firstName')!.focus()}
                            />
                        </div>
                        <div className='mb-4 md:w-1/2 md:ml-2'>
                            <label className={labelStyles} htmlFor='firstName'>
                                Keresztnév <span className='text-red-600'>*</span>
                            </label>
                            <Input 
                                id='firstName' values={[fields.firstName, setFieldSetter('firstName')]}
                                onEnter={() => document.getElementById('email')!.focus()}
                            />
                        </div>
                    </div>

                    <div className='flex flex-col md:flex-row'>
                        <div className='mb-4 md:w-1/2 md:mr-2'>
                            <label className={labelStyles} htmlFor='email'>
                                Email <span className='text-red-600'>*</span>
                            </label>
                            <Input 
                                id='email' values={[fields.email, setFieldSetter('email')]}
                                onEnter={() => document.getElementById('address')!.focus()}
                            />
                        </div>
                        <div className='mb-4 md:w-1/2 md:ml-2'>
                            <label className={labelStyles} htmlFor='address'>Lakcím</label>
                            <Input 
                                id='address' values={[fields.address, setFieldSetter('address')]}
                                onEnter={() => document.getElementById('phoneNumber')!.focus()}
                            />
                        </div>
                    </div>

                    <div className='flex flex-col md:flex-row'>
                        <div className='mb-4 md:w-1/2 md:mr-2'>
                            <label className={labelStyles} htmlFor='phoneNumber'>Telefonszám</label>
                            <Input 
                                id='phoneNumber' values={[fields.phoneNumber, setFieldSetter('phoneNumber')]}
                                onEnter={() => document.getElementById('notes')!.focus()}
                            />
                        </div>
                        <div className='mb-4 md:w-1/2 md:ml-2'></div>
                    </div>

                    <div className='mb-4'>
                        <label className={labelStyles} htmlFor='notes'>Egyéb megjegyzés</label>
                        <Input 
                            id='notes' values={[fields.notes, setFieldSetter('notes')]}
                        />
                    </div>

                    <div className='flex justify-center'>
                        <Button primary onClick={handleSubmitClick}>Regisztráció</Button>
                    </div>
                </div>
            </div>
        </CenteredFrame>
    </>;
}
