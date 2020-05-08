package bsep.pki.PublicKeyInfrastructure.exception;

import org.springframework.http.HttpStatus;

public class ApiInternalServerErrorException extends ApiException {
    private static final long serialVersionUID = 1L;

    public ApiInternalServerErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ApiInternalServerErrorException() {
        super("", HttpStatus.NOT_FOUND);
    }
}
