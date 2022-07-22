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
@ToString
public class UserDTO {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private boolean enabled;

}
