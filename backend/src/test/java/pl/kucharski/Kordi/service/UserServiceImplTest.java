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
import pl.kucharski.Kordi.dto.UserRegistrationDto;
import pl.kucharski.Kordi.entity.User;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.repository.UserRepository;
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
    private EmailValidator emailValidator;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl underTest;

    @BeforeEach
    void setUp() {
        emailValidator = new EmailValidator();
        passwordEncoder = new BCryptPasswordEncoder();
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
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            underTest.loadUserByUsername("testuser");
        });
        assertEquals("User not found in database", thrown.getMessage());
    }

    @Test
    void shouldThrowWhenUserNotVerified() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", false);
        given(userRepository.findUserByUsername("testuser")).willReturn(Optional.of(user));

        // when + then
        DisabledException thrown = assertThrows(DisabledException.class, () -> {
            underTest.loadUserByUsername("testuser");
        });
        assertEquals("User is not verified", thrown.getMessage());
    }

    @Test
    void shouldLoadsUser() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", true);
        given(userRepository.findUserByUsername("testuser")).willReturn(Optional.of(user));

        // when
        UserDetails userDetails = underTest.loadUserByUsername("testuser");

        // then
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    void shouldSaveUserWithEmailVerification() {
        // given
        UserRegistrationDto user = new UserRegistrationDto("Test", "test", "test123", "qwerty",
                "test@mail.com", "110339332");

        // when
        underTest.saveUser(user, false);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        verify(emailVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldSaveUserWithPhoneVerification() {
        // given
        UserRegistrationDto user = new UserRegistrationDto("Test", "test", "test123", "qwerty",
                "test@mail.com", "110339332");

        // when
        underTest.saveUser(user, true);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        verify(phoneVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldSendEmailVerificationWhenUserExistsAndIsDisabled() {
        // given
        UserRegistrationDto userDto = new UserRegistrationDto("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332");
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", false);
        given(userRepository.findUserByUsername("testuser")).willReturn(Optional.of(user));
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(user));
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(user));

        // when
        underTest.saveUser(userDto, false);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(emailVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldSendPhoneVerificationWhenUserExistsAndIsDisabled() {
        // given
        UserRegistrationDto userDto = new UserRegistrationDto("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332");
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", false);
        given(userRepository.findUserByUsername("testuser")).willReturn(Optional.of(user));
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(user));
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(user));

        // when
        underTest.saveUser(userDto, true);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(phoneVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        // given
        UserRegistrationDto user = new UserRegistrationDto("Test", "test", "test123", "qwerty",
                "@mail.com", "110339332");

        // when + then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            underTest.saveUser(user, false);
        });

        assertEquals("Email is not valid", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUsernameIsAlreadyInUse() {
        // given
        UserRegistrationDto userDto = new UserRegistrationDto("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332");
        User user = new User("Test2", "test2", "testuser", "qwerty",
                "test2@mail.com", "111339332", false);
        given(userRepository.findUserByUsername("testuser")).willReturn(Optional.of(user));

        // when + then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            underTest.saveUser(userDto, false);
        });

        assertEquals("Username is already in use!", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenEmailIsAlreadyInUse() {
        // given
        UserRegistrationDto userDto = new UserRegistrationDto("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332");
        User user = new User("Test2", "test2", "testuser", "qwerty",
                "test@mail.com", "111339332", false);
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(user));

        // when + then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            underTest.saveUser(userDto, false);
        });

        assertEquals("Email is already in use!", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPhoneIsAlreadyInUse() {
        // given
        UserRegistrationDto userDto = new UserRegistrationDto("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332");
        User user = new User("Test2", "test2", "testuser", "qwerty",
                "test@mail.com", "110339332", false);
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(user));

        // when + then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            underTest.saveUser(userDto, false);
        });

        assertEquals("Phone number is already in use!", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldEnableUsers() {
        // given
        User user = new User("Test", "test", "test123", "qwerty",
                "test@mail.com", "110339332", false);

        // when
        underTest.enableUser(user);

        // then
        ArgumentCaptor<String> userArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).enableUser(userArgumentCaptor.capture());
        String capturedUsername = userArgumentCaptor.getValue();
        assertEquals(user.getUsername(), capturedUsername);
    }

    @Test
    void shouldVerifyTokenOnEmailVerification() {
        // given
        User user = new User("Test", "test", "test123", "qwerty",
                "test@mail.com", "110339332", false);
        given(emailVerificationService.verify(user, "token")).willReturn("verified");

        // when
        String result = underTest.verifyToken(user, "token", false);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailVerificationService).verify(userArgumentCaptor.capture(), stringArgumentCaptor.capture());
        verify(userRepository).enableUser(stringArgumentCaptor.capture());
        assertEquals("verified", result);
    }

    @Test
    void shouldVerifyTokenOnPhoneVerification() {
        // given
        User user = new User("Test", "test", "test123", "qwerty",
                "test@mail.com", "110339332", false);
        given(phoneVerificationService.verify(user, "token")).willReturn("approved");

        // when
        String result = underTest.verifyToken(user, "token", true);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(phoneVerificationService).verify(userArgumentCaptor.capture(), stringArgumentCaptor.capture());
        verify(userRepository).enableUser(stringArgumentCaptor.capture());
        assertEquals("approved", result);
    }

    @Test
    void shouldGetUserById() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", true);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        User foundUser = underTest.getUserById(1L);

        // then
        assertEquals(user, foundUser);
    }

    @Test
    void shouldThrowWhenUserNotFoundById() {
        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            underTest.getUserById(1L);
        });
        assertEquals("User not found in database", thrown.getMessage());
    }

    @Test
    void shouldGetUserByUsername() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", true);
        given(userRepository.findUserByUsername("testuser")).willReturn(Optional.of(user));

        // when
        User foundUser = underTest.getUserByUsername("testuser");

        // then
        assertEquals(user, foundUser);
    }

    @Test
    void shouldThrowWhenUserNotFoundByUsername() {
        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            underTest.getUserByEmail("testuser");
        });
        assertEquals("User not found in database", thrown.getMessage());
    }

    @Test
    void shouldGetUserByEmail() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", true);
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(user));

        // when
        User foundUser = underTest.getUserByEmail("test@mail.com");

        // then
        assertEquals(user, foundUser);
    }

    @Test
    void shouldThrowWhenUserNotFoundByEmail() {
        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            underTest.getUserByEmail("test@mail.com");
        });
        assertEquals("User not found in database", thrown.getMessage());
    }

    @Test
    void shouldGetUserByPhone() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", true);
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(user));

        // when
        User foundUser = underTest.getUserByPhone("110339332");

        // then
        assertEquals(user, foundUser);
    }

    @Test
    void shouldThrowWhenUserNotFoundByPhone() {
        // given
        User user = new User("Test", "test", "testuser", "qwerty",
                "test@mail.com", "110339332", true);

        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            underTest.getUserByPhone("110339332");
        });
        assertEquals("User not found in database", thrown.getMessage());
    }

}