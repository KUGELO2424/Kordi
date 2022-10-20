package pl.kucharski.Kordi.aop;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kucharski.Kordi.exception.NotOwnerOfCollectionException;
import pl.kucharski.Kordi.model.CustomError;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {NotOwnerOfCollectionException.class})
    public ResponseEntity<?> handleException(NotOwnerOfCollectionException ex){
        CustomError error = new CustomError(HttpStatus.FORBIDDEN, ex.getMessage());
        return new ResponseEntity<Object>(
                error, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}
