package pl.kucharski.Kordi.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomError {

    private HttpStatus status;
    private String error;

    public CustomError(HttpStatus status, String message) {
        this.status = status;
        this.error = message;
    }

}
