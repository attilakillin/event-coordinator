import { NetworkService } from "./network-service";

/**
 * Service namespace with static functions that handle communication with the backend.
 * 
 * Every function returns a promise that fulfills when a connection is made and the
 * response is received, and fails in every other case.
 */
export namespace ArticleService {

    /**
     * Search the list of published articles with the given keywords filter.
     */
    export function searchPublished(keywords: string): Promise<any> {
        return NetworkService.publicAsJson({
            path: '/api/articles/published?keywords=' + encodeURIComponent(keywords),
            method: 'GET'
        });
    }

    /**
     * Search the list of draft articles with the given keywords filter.
     * Requires authentication.
     */
    export function searchDrafts(keywords: string): Promise<any> {
        return NetworkService.protectedAsJson({
            path: '/api/articles/drafts?keywords=' + encodeURIComponent(keywords),
            method: 'GET'
        });
    }

    /**
     * Retrieve an article. May require authentication, if the ID refers to a draft article.
     */
    export function get(id: string): Promise<any> {
        return NetworkService.protectedAsJson({
            path: '/api/articles/administer/' + id,
            method: 'GET',
            tryUnauthenticated: true
        });
    }

    /**
     * Publishes an article. Requires authentication.
     */
    export function publish(id: String): Promise<any> {
        return NetworkService.protectedAsJson({
            path: '/api/articles/administer/' + id + '/publish',
            method: 'POST'
        });
    }

    /**
     * Post an article. Requires authentication.
     */
    export function post(title: string, content: string): Promise<any> {
        return NetworkService.protectedWithBodyAsJson({
            path: '/api/articles/administer',
            method: 'POST',
            body: { title: title, content: content }
        });
    }

    /**
     * Modify an article with the given ID. Requires authentication.
     */
    export function put(id: string, title: string, content: string): Promise<any> {
        return NetworkService.protectedWithBodyAsJson({
            path: '/api/articles/administer/' + id,
            method: 'PUT',
            body: { title: title, content: content }
        });
    }

    /**
     * Deletes an article. Requires authentication.
     */
    export function remove(id: string): Promise<any> {
        return NetworkService.protectedAsJson({
            path: '/api/articles/administer/' + id,
            method: 'DELETE'
        });
    }
};
