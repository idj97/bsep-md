package bsep.pki.PublicKeyInfrastructure.exception;

import org.springframework.http.HttpStatus;

public class ApiServiceUnavailableException extends ApiException {
    private static final long serialVersionUID = 1L;

    public ApiServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ApiServiceUnavailableException() {
        super("", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
