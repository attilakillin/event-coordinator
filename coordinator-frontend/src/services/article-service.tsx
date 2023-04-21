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
        return fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title: title, content: content })
        })
            .then(r => (r.ok) ? r : Promise.reject());
    }

    export function put(id: string, title: string, content: string): Promise<any> {
        return fetch(url + '/' + id, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title: title, content: content })
        })
            .then(r => (r.ok) ? r : Promise.reject());
    }

    export function remove(id: string): Promise<any> {
        return fetch(url + '/' + id, { method: 'DELETE' })
            .then(r => (r.ok) ? r : Promise.reject());
    }
};
