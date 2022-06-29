package pl.kucharski.Kordi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kucharski.Kordi.entity.User;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByPhone(String phone);
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User a " +
            "SET a.enabled = TRUE WHERE a.username = ?1")
    void enableUser(String username);
}
