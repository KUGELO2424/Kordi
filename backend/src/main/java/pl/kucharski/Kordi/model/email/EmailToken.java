package pl.kucharski.Kordi.model.email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.kucharski.Kordi.model.BaseEntity;
import pl.kucharski.Kordi.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity that contains email verification token for specific user.
 * @see User
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Getter
@NoArgsConstructor
@Entity
@Table(name = "email_token")
public class EmailToken extends BaseEntity {

    @Column(name = "token")
    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public EmailToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, LocalDateTime confirmedAt,
                      User user) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.confirmedAt = confirmedAt;
        this.user = user;
    }
}
