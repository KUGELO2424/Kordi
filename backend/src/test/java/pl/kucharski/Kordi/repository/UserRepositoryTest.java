package pl.kucharski.Kordi.repository;

import org.h2.tools.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kucharski.Kordi.entity.User;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @BeforeAll
    public static void initTest() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
                .start();
    }

    @Test
    void shouldFindUserByUsername() {
        // given
        String username = "gelo2424";

        // when
        User user = underTest.findUserByUsername(username);

        // then
        assertEquals(user.getUsername(), username);
    }

    @Test
    void shouldFindUserByEmail() {
        // given
        String email = "adam@gmail.com";

        // when
        User user = underTest.findUserByEmail(email);

        // then
        assertEquals(user.getEmail(), email);
    }

    @Test
    void shouldFindUserByPhone() {
        // given
        String phone = "123123123";

        // when
        User user = underTest.findUserByPhone(phone);

        // then
        assertEquals(user.getPhone(), phone);
    }

    @Test
    void shouldEnableUser() throws InterruptedException {
        // given
        User user = underTest.findUserByUsername("test");
        assertFalse(user.isEnabled());

        // given
        underTest.enableUser("test");
        User updatedUser = underTest.findUserByUsername("test");

        // then
        assertTrue(updatedUser.isEnabled());
    }
}