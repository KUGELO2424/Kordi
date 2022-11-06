package pl.kucharski.Kordi.aop;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
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

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleException(MethodArgumentNotValidException ex){
        StringBuilder errorMessage = new StringBuilder();
        for (final ObjectError objectError : ex.getBindingResult().getAllErrors()) {
            errorMessage.append(objectError.getDefaultMessage()).append(".");
        }
        CustomError error = new CustomError(HttpStatus.BAD_REQUEST, errorMessage.toString());
        return new ResponseEntity<Object>(
                error, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<?> handleException(HttpMessageNotReadableException ex){
        CustomError error = new CustomError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<Object>(
                error, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    public ResponseEntity<?> handleException(ResponseStatusException ex){
        CustomError error = new CustomError(ex.getStatus(), ex.getReason());
        return new ResponseEntity<Object>(
                error, new HttpHeaders(), ex.getStatus());
    }

}
