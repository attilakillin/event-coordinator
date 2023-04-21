export namespace AuthService {
    const url = process.env.NEXT_PUBLIC_BACKEND_URL! + '/api/auth';

    export function login(username: string, password: string): Promise<any> {
        return fetch(url + '/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: username, password: password })
        })
            .then(r => (r.ok) ? r : Promise.reject())
            .then(r => r.json());
    }
};
