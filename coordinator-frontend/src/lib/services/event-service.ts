import { NetworkService } from "./network-service";

/**
 * Service namespace with static functions that handle communication with the backend.
 * 
 * Every function returns a promise that fulfills when a connection is made and the
 * response is received, and fails in every other case.
 */
export namespace EventService {

    /**
     * Search the list of events with the given keywords filter.
     */
    export function search(keywords: string): Promise<any> {
        return NetworkService.publicAsJson({
            path: '/api/events?keywords=' + encodeURIComponent(keywords),
            method: 'GET'
        });
    }

    /**
     * Retrieve an event. Does not require authentication, but the participant emails are not returned.
     */
    export function getSummary(id: string): Promise<any> {
        return NetworkService.publicAsJson({
            path: '/api/events/' + id,
            method: 'GET'
        });
    }

    /**
     * Retrieve an event. Requires authentication, but returns everything related to the event.
     */
    export function getDetails(id: string): Promise<any> {
        return NetworkService.protectedAsJson({
            path: '/api/events/administer/' + id,
            method: 'GET'
        });
    }

    /**
     * Post an event. Requires authentication.
     */
    export function post(title: string): Promise<any> {
        return NetworkService.protectedWithBodyAsJson({
            path: '/api/events/administer',
            method: 'POST',
            body: { title: title }
        });
    }

    /**
     * Modify an event with the given ID. Requires authentication.
     */
    export function put(id: string, title: string): Promise<any> {
        return NetworkService.protectedWithBodyAsJson({
            path: '/api/events/administer/' + id,
            method: 'PUT',
            body: { title: title }
        });
    }

    /**
     * Deletes an event. Requires authentication.
     */
    export function remove(id: string): Promise<any> {
        return NetworkService.protectedAsJson({
            path: '/api/events/administer/' + id,
            method: 'DELETE'
        });
    }

    /**
     * Register an email to an event.
     */
    export function registerToEvent(id: string, email: string): Promise<any> {
        return NetworkService.publicWithBodyAsJson({
            path: '/api/events/register/' + id,
            method: 'POST',
            body: { email: email }
        });
    }

    /**
     * Unregister an email from an event.
     */
    export function unregisterFromEvent(id: string, email: string): Promise<any> {
        return NetworkService.protectedWithBodyAsJson({
            path: '/api/events/unregister/' + id,
            method: 'POST',
            body: { email: email }
        });
    }
};
