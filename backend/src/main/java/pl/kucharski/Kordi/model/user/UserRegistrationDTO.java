package pl.kucharski.Kordi.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserRegistrationDTO {

    @NotBlank(message = "Firstname cannot be empty")
    private String firstName;
    @NotBlank(message = "Lastname cannot be empty")
    private String lastName;
    @NotBlank(message = "Username cannot be empty")
    private String username;
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @NotBlank(message = "Phone number cannot be empty")
    private String phone;

}
