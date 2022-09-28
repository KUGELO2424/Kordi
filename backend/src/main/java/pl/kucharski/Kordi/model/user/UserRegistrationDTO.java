package pl.kucharski.Kordi.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserRegistrationDTO {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String phone;

}
