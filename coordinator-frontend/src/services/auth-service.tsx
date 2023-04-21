export namespace AuthService {
    const url = process.env.NEXT_PUBLIC_BACKEND_URL! + '/api/auth';
    const id = 'auth-token';

    export function login(username: string, password: string): Promise<any> {
        return fetch(url + '/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: username, password: password })
        })
            .then(r => (r.ok) ? r.json() : Promise.reject())
            .then(r => {
                localStorage.setItem(id, r.token);
                return r;
            });
    }

    export function logout(): Promise<any> {
        localStorage.removeItem(id);
        return Promise.resolve();
    }

    export function validate(): Promise<any> {
        const token = localStorage.getItem(id);

        if (token === null) {
            return Promise.reject();
        }

        return fetch(url + '/validate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ token: token })
        })
            .then(r => (r.ok) ? r.json() : Promise.reject())
            .then(r => {
                if (!r.valid) {
                    localStorage.removeItem(id);
                    return Promise.reject();
                } else {
                    return r;
                }
            });
    }
};
