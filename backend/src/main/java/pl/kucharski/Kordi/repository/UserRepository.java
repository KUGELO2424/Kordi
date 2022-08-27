package pl.kucharski.Kordi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kucharski.Kordi.model.user.User;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByPhone(String phone);
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User a " +
            "SET a.enabled = TRUE WHERE a.username = ?1")
    void enableUser(String username);
}
