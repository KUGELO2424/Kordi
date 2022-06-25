package pl.kucharski.Kordi.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserRegistrationDto {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String phone;

}
