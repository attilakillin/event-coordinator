import { useEffect, useState } from "react";
import { AuthenticationService } from "../services/auth-service";

/**
 * An enum used to discern the current state of authentication.
 */
export enum AuthenticationStatus {
    PENDING, SUCCESS, FAILURE
};

/**
 * A React Hook that authenticates the user, and returns the current
 * authentication status.
 */
export function useAuthentication() {
    const [status, setStatus] = useState(AuthenticationStatus.PENDING);

    useEffect(() => {
        AuthenticationService.validate()
            .then(() => setStatus(AuthenticationStatus.SUCCESS))
            .catch(() => setStatus(AuthenticationStatus.FAILURE));
    }, []);

    return status;
};
