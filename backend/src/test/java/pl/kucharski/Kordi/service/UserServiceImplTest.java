package pl.kucharski.Kordi.service;

import org.junit.jupiter.api.Assertions;
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
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.exception.InvalidPasswordException;
import pl.kucharski.Kordi.exception.UserAlreadyVerifiedException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.exception.UserRegisterException;
import pl.kucharski.Kordi.model.user.PasswordEncoderMapper;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.UserMapper;
import pl.kucharski.Kordi.model.user.UserMapperImpl;
import pl.kucharski.Kordi.model.user.UserRegistrationDTO;
import pl.kucharski.Kordi.repository.UserRepository;
import pl.kucharski.Kordi.service.user.UserServiceImpl;
import pl.kucharski.Kordi.service.verification.VerificationService;
import pl.kucharski.Kordi.validator.EmailValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationService emailVerificationService;
    @Mock
    private VerificationService phoneVerificationService;
    private UserServiceImpl underTest;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String NEW_PASSWORD = "newPassword";
    private static final String NEW_TOO_SHORT_PASSWORD = "short";

    private static final User NOT_VERIFIED_USER = new User("Test", "test", "test123", "qwerty",
            "test@mail.com", "110339332", false, VerificationType.EMAIL);
    private static final User VERIFIED_USER = new User("Test", "test", "test123", "qwerty",
            "test@mail.com", "110339332", true, VerificationType.EMAIL);

    private static final UserDTO NOT_VERIFIED_USER_DTO= new UserDTO("Test", "test", "test123",
            "test@mail.com", "110339332", false, VerificationType.EMAIL);
    private static final UserDTO VERIFIED_USER_DTO= new UserDTO("Test", "test", "test123",
            "test@mail.com", "110339332", true, VerificationType.EMAIL);

    private static final UserRegistrationDTO USER_TO_REGISTER_1 = new UserRegistrationDTO("Test",
            "test", "test123", "ytr","test@mail.com", "110339332", VerificationType.EMAIL);
    private static final UserRegistrationDTO USER_TO_REGISTER_2 = new UserRegistrationDTO("Test",
            "test", "test1234", "qwerty","test@mail.com", "987142231", VerificationType.EMAIL);
    private static final UserRegistrationDTO USER_TO_REGISTER_3 = new UserRegistrationDTO("Test",
            "test", "test1234", "qwerty","test2@mail.com", "110339332", VerificationType.EMAIL);
    private static final UserRegistrationDTO INVALID_USER_TO_REGISTER = new UserRegistrationDTO("Test",
            "test", "test123", "qwerty","@mail.com", "110339332", VerificationType.EMAIL);

    @BeforeEach
    void setUp() {
        EmailValidator emailValidator = new EmailValidator();
        PasswordEncoderMapper passwordEncoderMapper = new PasswordEncoderMapper(new BCryptPasswordEncoder());
        UserMapper userMapper = new UserMapperImpl(passwordEncoderMapper);
        underTest = new UserServiceImpl(userRepository, emailValidator,
                emailVerificationService, phoneVerificationService, userMapper, passwordEncoder);
    }

    @Test
    void shouldGetAllUsers() {
        // given
        when(userRepository.findAll()).thenReturn(List.of(VERIFIED_USER, NOT_VERIFIED_USER));

        // when
        List<UserDTO> users = underTest.getUsers();

        // then
        assertEquals(2, users.size());
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
        // given
        USER_TO_REGISTER_1.setVerificationType(VerificationType.EMAIL);
        when(userRepository.findUserByUsername(USER_TO_REGISTER_1.getUsername()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(NOT_VERIFIED_USER));

        // when
        underTest.saveUser(USER_TO_REGISTER_1);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        Assertions.assertNotEquals(userArgumentCaptor.getValue().getPassword(), USER_TO_REGISTER_1.getPassword());
        assertTrue(userArgumentCaptor.getValue().getPassword().contains("$2a$10$"));
        verify(emailVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldSaveUserWithPhoneVerification() {
        // given
        USER_TO_REGISTER_1.setVerificationType(VerificationType.PHONE);
        when(userRepository.findUserByUsername(USER_TO_REGISTER_1.getUsername()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(NOT_VERIFIED_USER));

        // when
        underTest.saveUser(USER_TO_REGISTER_1);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        Assertions.assertNotEquals(userArgumentCaptor.getValue().getPassword(), USER_TO_REGISTER_1.getPassword());
        assertTrue(userArgumentCaptor.getValue().getPassword().contains("$2a$10$"));
        verify(phoneVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldSendEmailVerificationWhenUserExistsAndIsDisabled() {
        // given
        USER_TO_REGISTER_1.setVerificationType(VerificationType.EMAIL);
        given(userRepository.findUserByUsername("test123")).willReturn(Optional.of(NOT_VERIFIED_USER));
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(NOT_VERIFIED_USER));
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when
        underTest.saveUser(USER_TO_REGISTER_1);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(emailVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldSendPhoneVerificationWhenUserExistsAndIsDisabled() {
        //given
        USER_TO_REGISTER_1.setVerificationType(VerificationType.PHONE);
        given(userRepository.findUserByUsername("test123")).willReturn(Optional.of(NOT_VERIFIED_USER));
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(NOT_VERIFIED_USER));
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when
        underTest.saveUser(USER_TO_REGISTER_1);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(phoneVerificationService).send(userArgumentCaptor.capture());
    }

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        // when + then
        UserRegisterException thrown = assertThrows(UserRegisterException.class,
                () -> underTest.saveUser(INVALID_USER_TO_REGISTER));

        assertEquals("Email is not valid", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUsernameIsAlreadyInUse() {
        // given
        USER_TO_REGISTER_1.setVerificationType(VerificationType.EMAIL);
        given(userRepository.findUserByUsername("test123")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when + then
        UserRegisterException thrown = assertThrows(UserRegisterException.class,
                () -> underTest.saveUser(USER_TO_REGISTER_1));

        assertEquals("Username is already in use!", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenEmailIsAlreadyInUse() {
        // given
        USER_TO_REGISTER_2.setVerificationType(VerificationType.EMAIL);
        given(userRepository.findUserByEmail("test@mail.com")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when + then
        UserRegisterException thrown = assertThrows(UserRegisterException.class,
                () -> underTest.saveUser(USER_TO_REGISTER_2));

        assertEquals("Email is already in use!", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPhoneIsAlreadyInUse() {
        // given
        USER_TO_REGISTER_3.setVerificationType(VerificationType.EMAIL);
        given(userRepository.findUserByPhone("110339332")).willReturn(Optional.of(NOT_VERIFIED_USER));

        // when + then
        UserRegisterException thrown = assertThrows(UserRegisterException.class,
                () -> underTest.saveUser(USER_TO_REGISTER_3));

        assertEquals("Phone number is already in use!", thrown.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldVerifyTokenOnEmailVerification() {
        // given
        NOT_VERIFIED_USER_DTO.setVerificationType(VerificationType.EMAIL);
        given(emailVerificationService.verify(NOT_VERIFIED_USER_DTO, "token")).willReturn(VerificationStatus.VERIFIED);

        // when
        VerificationStatus result = underTest.verifyToken(NOT_VERIFIED_USER_DTO, "token");

        //then
        ArgumentCaptor<UserDTO> userArgumentCaptor = ArgumentCaptor.forClass(UserDTO.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailVerificationService).verify(userArgumentCaptor.capture(), stringArgumentCaptor.capture());
        verify(userRepository).enableUser(stringArgumentCaptor.capture());
        assertEquals(VerificationStatus.VERIFIED, result);
    }

    @Test
    void shouldVerifyTokenOnPhoneVerification() {
        // given
        NOT_VERIFIED_USER_DTO.setVerificationType(VerificationType.PHONE);
        given(phoneVerificationService.verify(NOT_VERIFIED_USER_DTO, "token")).willReturn(VerificationStatus.VERIFIED);

        // when
        VerificationStatus result = underTest.verifyToken(NOT_VERIFIED_USER_DTO, "token");

        //then
        ArgumentCaptor<UserDTO> userArgumentCaptor = ArgumentCaptor.forClass(UserDTO.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(phoneVerificationService).verify(userArgumentCaptor.capture(), stringArgumentCaptor.capture());
        verify(userRepository).enableUser(stringArgumentCaptor.capture());
        assertEquals(VerificationStatus.VERIFIED, result);
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
        // when + then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> underTest.getUserByPhone("110339332"));
        assertEquals("User not found in database", thrown.getMessage());
    }

    @Test
    void shouldUpdateUserPassword() {
        // given
        VERIFIED_USER.setPassword(passwordEncoder.encode("qwerty"));
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(VERIFIED_USER));

        // when
        underTest.updatePassword("testuser", "qwerty", NEW_PASSWORD);

        // then
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, VERIFIED_USER.getPassword()));
    }

    @Test
    void shouldThrowInvalidPasswordExceptionIfOldPasswordDoesNotMatch() {
        // given
        VERIFIED_USER.setPassword(passwordEncoder.encode("qwerty"));
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(VERIFIED_USER));

        // when + then
        assertThrows(InvalidPasswordException.class, () -> underTest.updatePassword("testuser", "wrongOldPassword", NEW_PASSWORD));
    }

    @Test
    void shouldThrowInvalidPasswordExceptionIfNewPasswordTooShort() {
        // given
        VERIFIED_USER.setPassword(passwordEncoder.encode("qwerty"));
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(VERIFIED_USER));

        // when + then
        assertThrows(InvalidPasswordException.class, () -> underTest.updatePassword("testuser", "wrongOldPassword", NEW_TOO_SHORT_PASSWORD));
    }

    @Test
    void shouldThrowUserNotFoundOnUpdatedUserPassword() {
        // given
        VERIFIED_USER.setPassword(passwordEncoder.encode("qwerty"));
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());

        // when + then
        assertThrows(UserNotFoundException.class, () -> underTest.updatePassword("testuser", "qwerty", NEW_PASSWORD));
    }

    @Test
    void shouldThrowUserUserAlreadyVerifiedOnSendVerificationToken() {
        // when + then
        assertThrows(UserAlreadyVerifiedException.class, () -> underTest.sendVerificationToken(VERIFIED_USER_DTO));
    }

    @Test
    void shouldSendVerificationToken() {
        // given
        when(userRepository.findUserByUsername(NOT_VERIFIED_USER_DTO.getUsername()))
                .thenReturn(Optional.of(NOT_VERIFIED_USER));

        // when
        underTest.sendVerificationToken(NOT_VERIFIED_USER_DTO);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(emailVerificationService).send(userArgumentCaptor.capture());
    }

}