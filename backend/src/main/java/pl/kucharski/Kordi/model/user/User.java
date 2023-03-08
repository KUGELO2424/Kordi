package pl.kucharski.Kordi.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Entity that represents account of app user.
 *
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account")
public class User extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean enabled;
    @Column(columnDefinition="ENUM('EMAIL', 'PHONE')")
    @Enumerated(EnumType.STRING)
    private VerificationType verificationType;

    public User(Long id, String firstName, String lastName, String username, String password, String email,
                String phone, boolean enabled, VerificationType verificationType) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.enabled = enabled;
        this.verificationType = verificationType;
    }

    public User(String firstName, String lastName, String username, String password, String email, String phone,
                boolean enabled, VerificationType verificationType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.enabled = enabled;
        this.verificationType = verificationType;
    }
}
