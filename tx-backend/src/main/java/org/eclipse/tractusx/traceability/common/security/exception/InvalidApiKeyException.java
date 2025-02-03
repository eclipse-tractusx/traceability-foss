package org.eclipse.tractusx.traceability.common.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidApiKeyException extends RuntimeException  {
    public InvalidApiKeyException(String message) {
        super(message);
    }
}
