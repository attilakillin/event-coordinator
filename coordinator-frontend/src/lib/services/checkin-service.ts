import { CheckinStatus } from "@/components/types/checkinstatus";
import { NetworkService } from "./network-service";
import { Client } from '@stomp/stompjs';
import { AuthenticationService } from "./auth-service";

/**
 * Service namespace with static functions that handle communication with the backend.
 * 
 * Every function returns a promise that fulfills when a connection is made and the
 * response is received, and fails in every other case.
 */
export namespace CheckinService {

    /**
     * Get the list of participants for an event with the given ID.
     */
    export function getForEvent(eventId: number): Promise<CheckinStatus[]> {
        return NetworkService.protectedAsJson({
            path: '/api/checkin/' + eventId,
            method: 'GET'
        });
    }

    /** One shared client instance. */
    let client: Client;
    /**
     * Connect to Websocket server.
     */
    export function connectWS(onReceiveStatus: (status: CheckinStatus) => void) {
        client = new Client({
            brokerURL: process.env.NEXT_PUBLIC_BACKEND_WS! + '/api/checkin/ws'
        });

        client.onConnect = () => {
            client.subscribe('/topic/checkins', (status) => onReceiveStatus(JSON.parse(status.body)));
        };

        client.activate();
    }

    export function sendStatusChangeWS(targetStatus: CheckinStatus) {
        const token = AuthenticationService.getToken()!;
        client.publish({
            destination: '/update',
            headers: { 'Auth-Token': token },
            body: JSON.stringify(targetStatus)
        });
    }

    /**
     * Disconnect from websocket server.
     */
    export function disconnectWS() {
        client.deactivate();
    }
};
