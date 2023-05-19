import { Participant } from "@/components/types/participant";
import { NetworkService } from "./network-service";

/**
 * Service namespace with static functions that handle communication with the backend.
 * 
 * Every function returns a promise that fulfills when a connection is made and the
 * response is received, and fails in every other case.
 */
export namespace ParticipantService {

    /**
     * Retrieve the list of all registered participants.
     * Requires authentication.
     */
    export function getAllParticipants(): Promise<any> {
        return NetworkService.protectedAsJson({
            path: '/api/participants',
            method: 'GET'
        });
    }

    /**
     * Post a participant. Does not require authentication.
     */
    export function post(participant: Participant): Promise<any> {
        return NetworkService.publicWithBodyAsJson({
            path: '/api/participants',
            method: 'POST',
            body: participant
        });
    }
    
    /**
     * Deletes a participant. Requires authentication.
     */
    export function remove(id: string): Promise<any> {
        return NetworkService.protectedAsJson({
            path: '/api/participants/' + id,
            method: 'DELETE'
        });
    }
};
