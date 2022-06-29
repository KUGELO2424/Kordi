package pl.kucharski.Kordi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kucharski.Kordi.entity.EmailToken;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class EmailTokenRepositoryTest {

    @Autowired
    private EmailTokenRepository underTest;

    @Test
    void shouldUpdateConfirmedAtToken() {
        // given
        String token = "qwerty123456";
        EmailToken tokenBeforeUpdate = underTest.findByToken(token).orElseThrow(() -> {
                    throw new NullPointerException("Token not found");
                }
        );
        assertNull(tokenBeforeUpdate.getConfirmedAt());
        LocalDateTime currentTime = LocalDateTime.now();

        // when
        underTest.updateConfirmedAt(token, currentTime);
        EmailToken tokenAfterUpdate = underTest.findByToken(token).orElseThrow(() -> {
                    throw new NullPointerException("Token not found");
                }
        );

        // then
        assertThat(tokenAfterUpdate.getConfirmedAt()).isEqualToIgnoringSeconds(currentTime);
    }


}