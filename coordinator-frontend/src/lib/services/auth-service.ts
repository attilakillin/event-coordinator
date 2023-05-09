import { NetworkService } from "./network-service";

/**
 * Service namespace with static functions that handle administrator authentication.
 */
export namespace AuthenticationService {
    /**
     * This key is used in the user's local storage to store the authentication token.
     */
    const id = 'auth-token';
    /**
     * If the token was recently validated, how long do we believe it is still valid without
     * rechecking with the server, in seconds.
     */
    const cacheTime = 10;

    /** 
     * Return the current timestamp, in seconds instead of the default milliseconds unit.
     */
    function now(): number {
        return Math.floor(Date.now() / 1000);
    }

    /**
     * Logs the user in. Returns a rejected promise if the login was unsuccessful.
     */
    export function login(username: string, password: string): Promise<any> {
        return NetworkService.publicWithBodyAsJson({
            path: '/api/auth/login',
            method: 'POST',
            body: { username: username, password: password }
        })
            .then(r => {
                const data = { token: r.token, username: username, checked: now() };
                localStorage.setItem(id, JSON.stringify(data));
                return r;
            });
    }

    /**
     * Logs the user out. In reality, this means that the auth token is deleted from storage.
     */
    export function logout(): Promise<any> {
        localStorage.removeItem(id);
        return Promise.resolve();
    }

    /**
     * Parses and returns the stored token, or null, if the token doesn't exist.
     */
    export function getToken(): string | null {
        const data = localStorage.getItem(id);
        if (data === null) {
            return null;
        }

        return JSON.parse(data).token;
    }

    /**
     * Validates the stored token. Based on the cache value, validation may happen
     * automatically.
     */
    export function validate(): Promise<any> {
        const data = localStorage.getItem(id);
        if (data === null) {
            return Promise.reject();
        }

        const token = JSON.parse(data);
        if (now() - token.checked < cacheTime) {
            return Promise.resolve();
        }

        return NetworkService.publicWithBodyAsJson({
            path: '/api/auth/validate',
            method: 'POST',
            body: { token: token.token }
        })
            .then(r => {
                if (!r.valid) {
                    logout();
                    return Promise.reject();
                } else {
                    const data = { token: token.token, username: token.username, checked: now() };
                    localStorage.setItem(id, JSON.stringify(data));
                    return r;
                }
            });
    }
};
