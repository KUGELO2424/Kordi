package pl.kucharski.Kordi.model.user;

import lombok.*;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class UserDTO {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private boolean enabled;

}
