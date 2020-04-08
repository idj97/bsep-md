package bsep.pki.PublicKeyInfrastructure.exception;

import org.springframework.http.HttpStatus;

public class ApiBadRequestException extends ApiException {
    private static final long serialVersionUID = 1L;

    public ApiBadRequestException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ApiBadRequestException() {
        super("", HttpStatus.NOT_FOUND);
    }
}
