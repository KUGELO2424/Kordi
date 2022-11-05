package pl.kucharski.Kordi.service.verification;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.rest.verify.v2.service.VerificationCheckCreator;
import com.twilio.rest.verify.v2.service.VerificationCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.exception.UserVerifyException;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.model.user.UserDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class PhoneVerificationServiceTest {

    @InjectMocks
    private PhoneVerificationService underTest;

    private MockedStatic<Verification> verificationMockedStatic;
    private MockedStatic<VerificationCheck> verificationCheckMockedStatic;
    private MockedStatic<Twilio> twilioMockedStatic;

    private static final User NOT_VERIFIED_USER = new User("Test", "test", "test123", "qwerty",
            "test@mail.com", "110339332", false, VerificationType.PHONE);

    private static final UserDTO NOT_VERIFIED_USER_DTO= new UserDTO("Test", "test", "test123",
            "test@mail.com", "110339332", false, VerificationType.PHONE);

    private static final String VALID_TOKEN = "valid_token";

    @BeforeEach
    void setUp() {
        underTest = new PhoneVerificationService();
        verificationMockedStatic = mockStatic(Verification.class);
        verificationCheckMockedStatic = mockStatic(VerificationCheck.class);
        twilioMockedStatic = mockStatic(Twilio.class);
    }

    @AfterEach
    public void close() {
        verificationMockedStatic.close();
        verificationCheckMockedStatic.close();
        twilioMockedStatic.close();
    }

    @Test
    void shouldReturnVerificationStatusPending() {
        // GIVEN
        VerificationCreator verificationCreatorMock = mock(VerificationCreator.class);
        Verification verification = mock(Verification.class);
        when(Verification.creator(isNull(), anyString(), anyString())).thenReturn(verificationCreatorMock);
        when(verificationCreatorMock.create()).thenReturn(verification);
        when(verification.getStatus()).thenReturn("pending");

        // WHEN
        VerificationStatus status = underTest.send(NOT_VERIFIED_USER);

        // THEN
        assertEquals(VerificationStatus.PENDING, status);
    }

    @Test
    void shouldReturnVerificationStatusREJECTED() {
        // GIVEN
        VerificationCreator verificationCreatorMock = mock(VerificationCreator.class);
        Verification verification = mock(Verification.class);
        when(Verification.creator(isNull(), anyString(), anyString())).thenReturn(verificationCreatorMock);
        when(verificationCreatorMock.create()).thenReturn(verification);
        when(verification.getStatus()).thenReturn("rejected");

        // WHEN
        VerificationStatus status = underTest.send(NOT_VERIFIED_USER);

        // THEN
        assertEquals(VerificationStatus.REJECTED, status);
    }

    @Test
    void shouldReturnVerificationStatusVERIFIED() {
        // GIVEN
        VerificationCheckCreator verificationCheckCreator = mock(VerificationCheckCreator.class);
        VerificationCheck verificationCheck = mock(VerificationCheck.class);
        when(VerificationCheck.creator(isNull(), anyString())).thenReturn(verificationCheckCreator);
        when(verificationCheckCreator.setTo(anyString())).thenReturn(verificationCheckCreator);
        when(verificationCheckCreator.create()).thenReturn(verificationCheck);
        when(verificationCheck.getValid()).thenReturn(true);

        // WHEN
        VerificationStatus status = underTest.verify(NOT_VERIFIED_USER_DTO, VALID_TOKEN);

        // THEN
        assertEquals(VerificationStatus.VERIFIED, status);
    }

    @Test
    void shouldThrowUserVerifyExceptionWhenCannotVerifyUser() {
        // GIVEN
        VerificationCheckCreator verificationCheckCreator = mock(VerificationCheckCreator.class);
        VerificationCheck verificationCheck = mock(VerificationCheck.class);
        when(VerificationCheck.creator(isNull(), anyString())).thenReturn(verificationCheckCreator);
        when(verificationCheckCreator.setTo(anyString())).thenReturn(verificationCheckCreator);
        when(verificationCheckCreator.create()).thenReturn(verificationCheck);
        when(verificationCheck.getValid()).thenReturn(false);

        // WHEN + THEN
        UserVerifyException thrown = assertThrows(UserVerifyException.class,
                () ->underTest.verify(NOT_VERIFIED_USER_DTO, VALID_TOKEN));
        assertEquals("Cannot verify your account", thrown.getMessage());

    }

}