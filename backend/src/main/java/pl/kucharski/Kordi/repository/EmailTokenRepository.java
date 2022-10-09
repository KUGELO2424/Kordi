package pl.kucharski.Kordi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kucharski.Kordi.model.email.EmailToken;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findByToken(String token);
    List<EmailToken> findByUserId(Long userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE EmailToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    void updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);
}
