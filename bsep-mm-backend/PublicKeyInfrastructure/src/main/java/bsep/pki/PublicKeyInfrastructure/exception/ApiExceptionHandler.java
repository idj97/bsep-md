package bsep.pki.PublicKeyInfrastructure.exception;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@CommonsLog
public class ApiExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<JsonNode> handleException(ApiException ex) {
        log.error("An exception has occurred:  " + ex.getClass() + "; " + ex.getMessage() + "; " + ex.getStatus());
        return new ResponseEntity<>(ex.getValidJson(), ex.getStatus());
    }

}
