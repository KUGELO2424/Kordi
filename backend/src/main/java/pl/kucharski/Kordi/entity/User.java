package pl.kucharski.Kordi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Entity that represents account of app user.
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "account")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean enabled;

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
