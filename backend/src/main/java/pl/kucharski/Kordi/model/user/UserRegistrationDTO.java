package pl.kucharski.Kordi.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.kucharski.Kordi.enums.VerificationType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static pl.kucharski.Kordi.config.ErrorCodes.EMAIL_CANNOT_BE_EMPTY;
import static pl.kucharski.Kordi.config.ErrorCodes.FIRSTNAME_CANNOT_BE_EMPTY;
import static pl.kucharski.Kordi.config.ErrorCodes.LASTNAME_CANNOT_BE_EMPTY;
import static pl.kucharski.Kordi.config.ErrorCodes.PASSWORD_TOO_SHORT;
import static pl.kucharski.Kordi.config.ErrorCodes.PHONE_CANNOT_BE_EMPTY;
import static pl.kucharski.Kordi.config.ErrorCodes.USERNAME_CANNOT_BE_EMPTY;
import static pl.kucharski.Kordi.config.ErrorCodes.VERIFICATION_TYPE_CANNOT_BE_EMPTY;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserRegistrationDTO {

    @NotBlank(message = FIRSTNAME_CANNOT_BE_EMPTY)
    private String firstName;
    @NotBlank(message = LASTNAME_CANNOT_BE_EMPTY)
    private String lastName;
    @NotBlank(message = USERNAME_CANNOT_BE_EMPTY)
    private String username;
    @Size(min = 6, message = PASSWORD_TOO_SHORT)
    private String password;
    @NotBlank(message = EMAIL_CANNOT_BE_EMPTY)
    private String email;
    @NotBlank(message = PHONE_CANNOT_BE_EMPTY)
    private String phone;
    @NotNull(message = VERIFICATION_TYPE_CANNOT_BE_EMPTY)
    private VerificationType verificationType;

}
