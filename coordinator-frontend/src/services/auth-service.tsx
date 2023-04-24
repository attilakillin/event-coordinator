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
                localStorage.setItem(id, JSON.stringify({
                    token: r.token,
                    checked: Math.floor(Date.now() / 1000)
                }));
                return r;
            });
    }

    export function logout(): Promise<any> {
        localStorage.removeItem(id);
        return Promise.resolve();
    }

    export function getToken(): string | null {
        const data = localStorage.getItem(id);

        if (data === null) {
            return null;
        }
        return JSON.parse(data).token;
    }

    export function validate(): Promise<any> {
        const data = localStorage.getItem(id);

        if (data === null) {
            return Promise.reject();
        }

        const token = JSON.parse(data);
        if (Math.floor(Date.now() / 1000) - token.checked < 10) {
            return Promise.resolve();
        }

        return fetch(url + '/validate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ token: token.token })
        })
            .then(r => (r.ok) ? r.json() : Promise.reject())
            .then(r => {
                if (!r.valid) {
                    localStorage.removeItem(id);
                    return Promise.reject();
                } else {
                    localStorage.setItem(id, JSON.stringify({
                        token: token.token,
                        checked: Math.floor(Date.now() / 1000)
                    }));
                    return r;
                }
            });
    }
};
