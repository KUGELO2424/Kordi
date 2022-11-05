package pl.kucharski.Kordi.service.verification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.email.EmailToken;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.exception.UserVerifyException;

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


    private static final User NOT_VERIFIED_USER = new User("Test", "test", "test123", "qwerty",
            "test@mail.com", "110339332", false, VerificationType.EMAIL);

    private static final UserDTO NOT_VERIFIED_USER_DTO= new UserDTO("Test", "test", "test123",
            "test@mail.com", "110339332", false, VerificationType.EMAIL);

    @BeforeEach
    void setUp() {
        underTest = new EmailVerificationService(mailSender, tokenService);
    }

    @Test
    void shouldSendMail() {
        // given
        MimeMessage mimeMessage = realMailSender.createMimeMessage();
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        underTest.send(NOT_VERIFIED_USER);

        //then
        ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(mimeMessageArgumentCaptor.capture());
    }

    @Test
    void shouldVerifyEmail() {
        // given
        String token = "testToken";
        EmailToken emailToken = new EmailToken("testToken", LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), null, NOT_VERIFIED_USER);
        given(tokenService.getToken("testToken")).willReturn(Optional.of(emailToken));

        // when
        VerificationStatus result = underTest.verify(NOT_VERIFIED_USER_DTO, token);

        // then
        assertEquals(VerificationStatus.VERIFIED, result);
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        // given
        String token = "testToken";

        // when + then
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> underTest.verify(NOT_VERIFIED_USER_DTO, token));
        assertEquals("token not found", thrown.getMessage());
    }

    @Test
    void shouldThrowWhenEmailIsAlreadyVerified() {
        // given
        String token = "testToken";
        EmailToken emailToken = new EmailToken("testToken", LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), LocalDateTime.now().plusMinutes(1), NOT_VERIFIED_USER);
        given(tokenService.getToken("testToken")).willReturn(Optional.of(emailToken));

        // when + then
        UserVerifyException thrown = assertThrows(UserVerifyException.class,
                () -> underTest.verify(NOT_VERIFIED_USER_DTO, token));
        assertEquals("Email already confirmed", thrown.getMessage());
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        // given
        String token = "testToken";
        EmailToken emailToken = new EmailToken("testToken", LocalDateTime.now().minusMinutes(15),
                LocalDateTime.now().minusMinutes(1), null, NOT_VERIFIED_USER);
        given(tokenService.getToken("testToken")).willReturn(Optional.of(emailToken));

        // when + then
        UserVerifyException thrown = assertThrows(UserVerifyException.class,
                () -> underTest.verify(NOT_VERIFIED_USER_DTO, token));
        assertEquals("Token expired", thrown.getMessage());
    }
}