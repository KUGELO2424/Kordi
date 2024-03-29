package pl.kucharski.Kordi.model.user;

import lombok.*;
import pl.kucharski.Kordi.enums.VerificationType;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private boolean enabled;
    private VerificationType verificationType;

}
