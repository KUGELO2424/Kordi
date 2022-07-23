package pl.kucharski.Kordi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.UserRegistrationDTO;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.exception.UserRegisterException;
import pl.kucharski.Kordi.repository.UserRepository;
import pl.kucharski.Kordi.service.user.UserServiceImpl;
import pl.kucharski.Kordi.service.verification.VerificationService;
import pl.kucharski.Kordi.validator.EmailValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationService emailVerificationService;
    @Mock
    private VerificationService phoneVerificationService;
    private UserServiceImpl underTest;

    private static final User NOT_VERIFIED_USER = new User("Test", "test", "test123", "qwerty",
            "test@mail.com", "110339332", false);
    private static final User VERIFIED_USER = new User("Test", "test", "test123", "qwerty",
            "test@mail.com", "110339332", true);

    private static final UserDTO NOT_VERIFIED_USER_DTO= new UserDTO("Test", "test", "test123",
            "test@mail.com", "110339332", false);
    private static final UserDTO VERIFIED_USER_DTO= new UserDTO("Test", "test", "test123",
            "test@mail.com", "110339332", true);

    private static final UserRegistrationDTO USER_TO_REGISTER_1 = new UserRegistrationDTO("Test",
            "test", "test123", "qwerty","test@mail.com", "110339332");
    private static final UserRegistrationDTO USER_TO_REGISTER_2 = new UserRegistrationDTO("Test",
            "test", "test1234", "qwerty","test@mail.com", "987142231");
    private static final UserRegistrationDTO USER_TO_REGISTER_3 = new UserRegistrationDTO("Test",
            "test", "test1234", "qwerty","test2@mail.com", "110339332");
    private static final UserRegistrationDTO INVALID_USER_TO_REGISTER = new UserRegistrationDTO("Test",
            "test", "test123", "qwerty","@mail.com", "110339332");

    @BeforeEach
    void setUp() {
        EmailValidator emailValidator = new EmailValidator();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        underTest = new UserServiceImpl(userRepository, passwordEncoder, emailValidator,
                emailVerificationService, phoneVerificationService);
    }

    @Test
    void shouldGetAllUsers() {
        // when
        underTest.getUsers();

        // then
        verify(userRepository).findAll();
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> underTest.loadUserByUsername("testuser"));
        assertEquals("User not found in database", thrown.getMessage());
    }

    @Test
    void shouldThrowWhenUserNotVerified() {
        // given
        given(userRepository.findUserByUsername("test123")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when + then
        DisabledException thrown = assertThrows(DisabledException.class,
                () -> underTest.loadUserByUsername("test123"));
        assertEquals("User is not verified", thrown.getMessage());
    }

    @Test
    void shouldLoadsUser() {
        // given
        given(userRepository.findUserByUsername("test123")).willReturn(Optional.of(VERIFIED_USER));

        // when
        UserDetails userDetails = underTest.loadUserByUsername("test123");

        // then
        assertEquals("test123", userDetails.getUsername());
    }

    @Test
    void shouldSaveUserWithEmailVerification() {
        // give + when
        underTest.saveUser(USER_TO_REGISTER_1, false);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        verify(emailVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldSaveUserWithPhoneVerification() {
        // given + when
        underTest.saveUser(USER_TO_REGISTER_1, true);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        verify(phoneVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldSendEmailVerificationWhenUserExistsAndIsDisabled() {
        // given
        given(userRepository.findUserByUsername("test123")).willReturn(Optional.of(NOT_VERIFIED_USER));
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(NOT_VERIFIED_USER));
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when
        underTest.saveUser(USER_TO_REGISTER_1, false);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(emailVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldSendPhoneVerificationWhenUserExistsAndIsDisabled() {
        //given
        given(userRepository.findUserByUsername("test123")).willReturn(Optional.of(NOT_VERIFIED_USER));
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(NOT_VERIFIED_USER));
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when
        underTest.saveUser(USER_TO_REGISTER_1, true);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(phoneVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        // when + then
        UserRegisterException thrown = assertThrows(UserRegisterException.class,
                () -> underTest.saveUser(INVALID_USER_TO_REGISTER, false));

        assertEquals("Email is not valid", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUsernameIsAlreadyInUse() {
        // given
        given(userRepository.findUserByUsername("test123")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when + then
        UserRegisterException thrown = assertThrows(UserRegisterException.class,
                () -> underTest.saveUser(USER_TO_REGISTER_1, false));

        assertEquals("Username is already in use!", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenEmailIsAlreadyInUse() {
        // given
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when + then
        UserRegisterException thrown = assertThrows(UserRegisterException.class,
                () -> underTest.saveUser(USER_TO_REGISTER_2, false));

        assertEquals("Email is already in use!", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPhoneIsAlreadyInUse() {
        // given
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when + then
        UserRegisterException thrown = assertThrows(UserRegisterException.class,
                () -> underTest.saveUser(USER_TO_REGISTER_3, false));

        assertEquals("Phone number is already in use!", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldVerifyTokenOnEmailVerification() {
        // given
        given(emailVerificationService.verify(NOT_VERIFIED_USER_DTO, "token")).willReturn("verified");

        // when
        String result = underTest.verifyToken(NOT_VERIFIED_USER_DTO, "token", false);

        //then
        ArgumentCaptor<UserDTO> userArgumentCaptor = ArgumentCaptor.forClass(UserDTO.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailVerificationService).verify(userArgumentCaptor.capture(), stringArgumentCaptor.capture());
        verify(userRepository).enableUser(stringArgumentCaptor.capture());
        assertEquals("verified", result);
    }

    @Test
    void shouldVerifyTokenOnPhoneVerification() {
        // given
        given(phoneVerificationService.verify(NOT_VERIFIED_USER_DTO, "token")).willReturn("approved");

        // when
        String result = underTest.verifyToken(NOT_VERIFIED_USER_DTO, "token", true);

        //then
        ArgumentCaptor<UserDTO> userArgumentCaptor = ArgumentCaptor.forClass(UserDTO.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(phoneVerificationService).verify(userArgumentCaptor.capture(), stringArgumentCaptor.capture());
        verify(userRepository).enableUser(stringArgumentCaptor.capture());
        assertEquals("approved", result);
    }

    @Test
    void shouldGetUserById() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when
        UserDTO foundUser = underTest.getUserById(1L);

        // then
        assertEquals(NOT_VERIFIED_USER_DTO, foundUser);
    }

    @Test
    void shouldThrowWhenUserNotFoundById() {
        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> underTest.getUserById(1L));
        assertEquals("User not found in database", thrown.getMessage());
    }

    @Test
    void shouldGetUserByUsername() {
        // given
        given(userRepository.findUserByUsername("testuser")).willReturn(Optional.of(VERIFIED_USER));

        // when
        UserDTO foundUser = underTest.getUserByUsername("testuser");

        // then
        assertEquals(VERIFIED_USER_DTO, foundUser);
    }

    @Test
    void shouldThrowWhenUserNotFoundByUsername() {
        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> underTest.getUserByEmail("testuser"));
        assertEquals("User not found in database", thrown.getMessage());
    }

    @Test
    void shouldGetUserByEmail() {
        // given
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(VERIFIED_USER));

        // when
        UserDTO foundUser = underTest.getUserByEmail("test@mail.com");

        // then
        assertEquals(VERIFIED_USER_DTO, foundUser);
    }

    @Test
    void shouldThrowWhenUserNotFoundByEmail() {
        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> underTest.getUserByEmail("test@mail.com"));
        assertEquals("User not found in database", thrown.getMessage());
    }

    @Test
    void shouldGetUserByPhone() {
        // given
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(VERIFIED_USER));

        // when
        UserDTO foundUser = underTest.getUserByPhone("110339332");

        // then
        assertEquals(VERIFIED_USER_DTO, foundUser);
    }

    @Test
    void shouldThrowWhenUserNotFoundByPhone() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", true);

        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> underTest.getUserByPhone("110339332"));
        assertEquals("User not found in database", thrown.getMessage());
    }

}