package pl.kucharski.Kordi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @Test
    void shouldFindUserByUsername() {
        // given
        String username = "gelo2424";

        // when
        User user = underTest.findUserByUsername(username).orElseThrow(UserNotFoundException::new);

        // then
        assertEquals(user.getUsername(), username);
    }

    @Test
    void shouldFindUserByEmail() {
        // given
        String email = "adam@gmail.com";

        // when
        User user = underTest.findUserByEmail(email).orElseThrow(UserNotFoundException::new);

        // then
        assertEquals(user.getEmail(), email);
    }

    @Test
    void shouldFindUserByPhone() {
        // given
        String phone = "123123123";

        // when
        User user = underTest.findUserByPhone(phone).orElseThrow(UserNotFoundException::new);

        // then
        assertEquals(user.getPhone(), phone);
    }

    @Test
    void shouldEnableUser() {
        // given
        User user = underTest.findUserByUsername("test").orElseThrow(UserNotFoundException::new);
        assertFalse(user.isEnabled());

        // given
        underTest.enableUser("test");
        User updatedUser = underTest.findUserByUsername("test").orElseThrow(UserNotFoundException::new);

        // then
        assertTrue(updatedUser.isEnabled());
    }
}