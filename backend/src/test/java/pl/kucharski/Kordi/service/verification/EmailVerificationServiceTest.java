package pl.kucharski.Kordi.service.verification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.kucharski.Kordi.entity.EmailToken;
import pl.kucharski.Kordi.entity.User;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailTokenService tokenService;
    @Mock
    private JavaMailSender mailSender;

    private final JavaMailSender realMailSender = new JavaMailSenderImpl();

    private VerificationService underTest;


    @BeforeEach
    void setUp() {
        underTest = new EmailVerificationService(mailSender, tokenService);
    }

    @Test
    void shouldSendMail() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", false);
        MimeMessage mimeMessage = realMailSender.createMimeMessage();
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        underTest.send(user);

        //then
        ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(mimeMessageArgumentCaptor.capture());
    }

    @Test
    void shouldVerifyEmail() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", false);
        String token = "testToken";
        EmailToken emailToken = new EmailToken("testToken", LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), null, null);
        given(tokenService.getToken("testToken")).willReturn(Optional.of(emailToken));

        // when
        String result = underTest.verify(user, token);

        // then
        assertEquals("verified", result);
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", false);
        String token = "testToken";

        // when + then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            underTest.verify(user, token);
        });
        assertEquals("token not found", thrown.getMessage());
    }

    @Test
    void shouldThrowWhenEmailIsAlreadyVerified() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", false);
        String token = "testToken";
        EmailToken emailToken = new EmailToken("testToken", LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), LocalDateTime.now().plusMinutes(1), null);
        given(tokenService.getToken("testToken")).willReturn(Optional.of(emailToken));

        // when + then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            underTest.verify(user, token);
        });
        assertEquals("Email already confirmed", thrown.getMessage());
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", false);
        String token = "testToken";
        EmailToken emailToken = new EmailToken("testToken", LocalDateTime.now().minusMinutes(15),
                LocalDateTime.now().minusMinutes(1), null, null);
        given(tokenService.getToken("testToken")).willReturn(Optional.of(emailToken));

        // when + then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            underTest.verify(user, token);
        });
        assertEquals("Token expired", thrown.getMessage());
    }
}