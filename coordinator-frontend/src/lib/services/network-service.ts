import { AuthenticationService } from "./auth-service";

/**
 * Encapsulates functions related to network communication.
 * Used as an abstraction over the fetch API.
 */
export namespace NetworkService {
    /** Base URL that is shared by all service endpoints. */
    const url = process.env.NEXT_PUBLIC_BACKEND_URL!;

    /** Parameter types for non-modifying functions. */
    interface NoBodyParameters {
        path: string,
        method: string
    };

    /** Parameter types for modifying functions. */
    interface WithBodyParameters {
        path: string,
        method: string,
        body: object
    };

    /** Parameter types for protected endpoints. */
    interface ProtectedParameters {
        tryUnauthenticated?: boolean
    };

    /**
     * Tries to get the response body as a JSON object.
     * If no JSON body is present, an empty object is returned.
     */
    function tryJson(r: Response): Promise<any> {
        return r.json().catch(() => ({}));
    }

    /**
     * Execute a network call to a 'public', non-modifying API endpoint.
     * These endpoints do not require authentication, and do not upload data to the server.
     * 
     * On success, returns the response body as a JSON object in the resolved promise,
     * else returns a rejected promise.
     */
    export function publicAsJson(params: NoBodyParameters): Promise<any> {
        return fetch(url + params.path, {
            method: params.method
        })
            .then(r => (r.ok) ? tryJson(r) : Promise.reject());
    }

    /**
     * Execute a network call to a 'protected', but non-modifying API endpoint.
     * These endpoints require authentication, but do not upload data to the server.
     * 
     * If 'try unauthenticated' is set, the request is sent even if no authentication
     * token is present.
     * 
     * On success, returns the response body as a JSON object in the resolved promise,
     * else (for example with invalid authentication) returns a rejected promise.
     */
    export function protectedAsJson(params: NoBodyParameters & ProtectedParameters): Promise<any> {
        const token = AuthenticationService.getToken();
        if (token === null && !params.tryUnauthenticated) {
            return Promise.reject();
        }

        return fetch(url + params.path, {
            method: params.method,
            headers: {
                'Auth-Token': token || ''
            }
        })
            .then(r => (r.ok) ? tryJson(r) : Promise.reject());
    }

    /**
     * Execute a network call to a 'public', and modifying API endpoint.
     * These endpoints do not require authentication, but upload JSON objects to the server.
     * 
     * On success, returns the response body as a JSON object in the resolved promise,
     * else returns a rejected promise.
     */
    export function publicWithBodyAsJson(params: WithBodyParameters): Promise<any> {
        return fetch(url + params.path, {
            method: params.method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(params.body)
        })
            .then(r => (r.ok) ? tryJson(r) : Promise.reject());
    }

    /**
     * Execute a network call to a 'protected', and modifying API endpoint.
     * These endpoints require authentication, and upload JSON objects to the server.
     * 
     * If 'try unauthenticated' is set, the request is sent even if no authentication
     * token is present.
     * 
     * On success, returns the response body as a JSON object in the resolved promise,
     * else (for example with invalid authentication) returns a rejected promise.
     */
    export function protectedWithBodyAsJson(params: WithBodyParameters & ProtectedParameters): Promise<any> {
        const token = AuthenticationService.getToken();
        if (token === null && !params.tryUnauthenticated) {
            return Promise.reject();
        }

        return fetch(url + params.path, {
            method: params.method,
            headers: {
                'Auth-Token': token || '',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(params.body)
        })
            .then(r => (r.ok) ? tryJson(r) : Promise.reject());
    }
}
