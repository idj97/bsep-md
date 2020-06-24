package bsep.sc.SiemCenter.exception;

import org.springframework.http.HttpStatus;

public class ApiRuleInvalidException extends ApiException {

    public ApiRuleInvalidException(String message) {
        super(message, HttpStatus.NOT_ACCEPTABLE);
    }
}
