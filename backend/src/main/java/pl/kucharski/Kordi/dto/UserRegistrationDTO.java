package pl.kucharski.Kordi.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserRegistrationDTO {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String phone;

}
