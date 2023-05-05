import { AuthenticationService } from "./auth-service";

export namespace ArticleService {
    const url = process.env.NEXT_PUBLIC_BACKEND_URL! + '/api/articles';

    export function searchPublished(keywords: string): Promise<any> {
        return fetch(url + '/published?keywords=' + encodeURIComponent(keywords), { method: 'GET' })
            .then(r => (r.ok) ? r.json() : Promise.reject());
    }

    export function searchDrafts(keywords: string): Promise<any> {
        const token = AuthenticationService.getToken()
        if (token === null) {
            return Promise.reject();
        }

        return fetch(url + '/drafts?keywords=' + encodeURIComponent(keywords), {
            method: 'GET',
            headers: {
                'Auth-Token': token
            }
        })
            .then(r => (r.ok) ? r.json() : Promise.reject());
    }

    export function get(id: string): Promise<any> {
        const token = AuthenticationService.getToken();
        
        return fetch(url + '/administer/' + id, {
            method: 'GET',
            headers: {
                'Auth-Token': token || ''
            }
        })
            .then(r => (r.ok) ? r.json() : Promise.reject());
    }

    export function publish(id: String): Promise<any> {
        const token = AuthenticationService.getToken()
        if (token === null) {
            return Promise.reject();
        }

        return fetch(url + '/administer/' + id + '/publish', {
            method: 'POST',
            headers: {
                'Auth-Token': token
            }
        })
            .then(r => (r.ok) ? r : Promise.reject());
    }

    export function post(title: string, content: string): Promise<any> {
        const token = AuthenticationService.getToken()
        if (token === null) {
            return Promise.reject();
        }

        return fetch(url + '/administer', {
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
        const token = AuthenticationService.getToken()
        if (token === null) {
            return Promise.reject();
        }

        return fetch(url + '/administer/' + id, {
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
        const token = AuthenticationService.getToken()
        if (token === null) {
            return Promise.reject();
        }

        return fetch(url + '/administer/' + id, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Auth-Token': token
            },
        })
            .then(r => (r.ok) ? r : Promise.reject());
    }
};
