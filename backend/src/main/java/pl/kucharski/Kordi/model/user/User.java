package pl.kucharski.Kordi.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kucharski.Kordi.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity that represents account of app user.
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
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

    public User(Long id, String firstName, String lastName, String username, String password, String email,
                String phone, boolean enabled) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.enabled = enabled;
    }

    public User(String firstName, String lastName, String username, String password, String email, String phone,
                boolean enabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.enabled = enabled;
    }
}
