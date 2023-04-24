import { AuthService } from "./auth-service";

export namespace ArticleService {
    const url = process.env.NEXT_PUBLIC_BACKEND_URL! + '/api/articles';

    export function search(keywords: string): Promise<any> {
        return fetch(url + '?keywords=' + encodeURIComponent(keywords), { method: 'GET' })
            .then(r => (r.ok) ? r.json() : Promise.reject());
    }

    export function get(id: string): Promise<any> {
        return fetch(url + '/' + id, { method: 'GET' })
            .then(r => (r.ok) ? r.json() : Promise.reject());
    }

    export function post(title: string, content: string): Promise<any> {
        const token = AuthService.getToken()
        if (token === null) {
            return Promise.reject();
        }

        return fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Auth-Token': token
            },
            body: JSON.stringify({ title: title, content: content })
        })
            .then(r => (r.ok) ? r : Promise.reject());
    }

    export function put(id: string, title: string, content: string): Promise<any> {
        const token = AuthService.getToken()
        if (token === null) {
            return Promise.reject();
        }

        return fetch(url + '/' + id, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Auth-Token': token
            },
            body: JSON.stringify({ title: title, content: content })
        })
            .then(r => (r.ok) ? r : Promise.reject());
    }

    export function remove(id: string): Promise<any> {
        const token = AuthService.getToken()
        if (token === null) {
            return Promise.reject();
        }

        return fetch(url + '/' + id, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Auth-Token': token
            },
        })
            .then(r => (r.ok) ? r : Promise.reject());
    }
};
