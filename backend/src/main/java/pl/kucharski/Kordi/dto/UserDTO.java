package pl.kucharski.Kordi.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserDTO {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private boolean enabled;

}
