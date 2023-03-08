package pl.kucharski.Kordi.service.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.exception.InvalidPasswordException;
import pl.kucharski.Kordi.exception.UserAlreadyVerifiedException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.exception.UserRegisterException;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.UserMapper;
import pl.kucharski.Kordi.model.user.UserRegistrationDTO;
import pl.kucharski.Kordi.repository.UserRepository;
import pl.kucharski.Kordi.service.verification.VerificationService;
import pl.kucharski.Kordi.validator.EmailValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pl.kucharski.Kordi.config.ErrorCodes.EMAIL_ALREADY_EXISTS;
import static pl.kucharski.Kordi.config.ErrorCodes.EMAIL_NOT_VALID;
import static pl.kucharski.Kordi.config.ErrorCodes.PASSWORD_OLD_DOES_NOT_MATCH;
import static pl.kucharski.Kordi.config.ErrorCodes.PASSWORD_TOO_SHORT;
import static pl.kucharski.Kordi.config.ErrorCodes.PHONE_ALREADY_EXISTS;
import static pl.kucharski.Kordi.config.ErrorCodes.USERNAME_ALREADY_EXISTS;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_ALREADY_VERIFIED;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_FOUND;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    private final VerificationService emailVerificationService;
    private final VerificationService phoneVerificationService;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, EmailValidator emailValidator,
                           @Qualifier("emailVerificationService") VerificationService emailVerificationService,
                           @Qualifier("phoneVerificationService") VerificationService phoneVerificationService,
                           UserMapper userMapper, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailValidator = emailValidator;
        this.emailVerificationService = emailVerificationService;
        this.phoneVerificationService = phoneVerificationService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @see UserDetailsService#loadUserByUsername(String)
     * @throws DisabledException when user found but is not enabled
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    /**
     * @see UserService#saveUser(UserRegistrationDTO)
     */
    @Override
    @Transactional
    public VerificationStatus saveUser(UserRegistrationDTO user) {
        UserDTO userDTO = userMapper.mapToUserDTO(user);
        User foundUserByEmail = userRepository.findUserByEmail(userDTO.getEmail()).orElse(null);
        User foundUserByUsername = userRepository.findUserByUsername(userDTO.getUsername()).orElse(null);
        User foundUserByPhone = userRepository.findUserByPhone(userDTO.getPhone()).orElse(null);

        // If given user attributes are the same, send verification token again
        if (foundUserByUsername != null && foundUserByEmail != null && foundUserByPhone != null) {
            if (foundUserByUsername.equals(foundUserByEmail) && foundUserByUsername.equals(foundUserByPhone)
                    && !foundUserByEmail.isEnabled()) {
                return sendVerificationToken(userDTO);
            }
        }

        if (!emailValidator.test(user.getEmail())) {
            throw new UserRegisterException(EMAIL_NOT_VALID);
        } else if (foundUserByUsername != null) {
            throw new UserRegisterException(USERNAME_ALREADY_EXISTS);
        } else if (foundUserByEmail != null) {
            throw new UserRegisterException(EMAIL_ALREADY_EXISTS);
        } else if (foundUserByPhone != null) {
            throw new UserRegisterException(PHONE_ALREADY_EXISTS);
        }

        User newUser = userMapper.mapToUser(user);
        userRepository.save(newUser);
        return sendVerificationToken(userDTO);
    }

    /**
     * @see UserService#sendVerificationToken(UserDTO)
     */
    @Override
    @Transactional
    public VerificationStatus sendVerificationToken(UserDTO userDTO) {
        if (userDTO.isEnabled()) {
            throw new UserAlreadyVerifiedException(USER_ALREADY_VERIFIED);
        }
        User user = userRepository.findUserByUsername(userDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        if (userDTO.getVerificationType() == VerificationType.PHONE) {
            return registerUserWithPhoneVerification(user);
        } else {
            return registerUserWithEmailVerification(user);
        }
    }

    /**
     * @see UserService#verifyToken(UserDTO, String)
     */
    @Override
    @Transactional
    public VerificationStatus verifyToken(UserDTO user, String token) {
        VerificationStatus response;
        if (user.getVerificationType() == VerificationType.PHONE) {
            response = phoneVerificationService.verify(user, token);
        } else {
            response = emailVerificationService.verify(user, token);
        }
       if (response.equals(VerificationStatus.VERIFIED)) {
           enableUser(user);
       }
       return response;
    }

    /**
     * @see UserService#updatePassword(String, String, String)
     */
    @Override
    @Transactional
    public void updatePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException(PASSWORD_OLD_DOES_NOT_MATCH);
        }
        if (newPassword.length() < 6) {
            throw new InvalidPasswordException(PASSWORD_TOO_SHORT);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    /**
     * @see UserService#getUserById(long)
     */
    @Override
    public UserDTO getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return userMapper.mapToUserDTO(user);
    }

    /**
     * @see UserService#getUserByUsername(String) (long)
     */
    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return userMapper.mapToUserDTO(user);
    }

    /**
     * @see UserService#getUserByEmail(String)
     */
    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return userMapper.mapToUserDTO(user);
    }

    /**
     * @see UserService#getUserByPhone(String)
     */
    @Override
    public UserDTO getUserByPhone(String phone) {
        User user = userRepository.findUserByPhone(phone)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return userMapper.mapToUserDTO(user);
    }

    /**
     * @see UserService#getUsers()
     */
    @Override
    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream().map(userMapper::mapToUserDTO).collect(Collectors.toList());
    }

    private void enableUser(UserDTO user) {
        userRepository.enableUser(user.getUsername());
    }


    private VerificationStatus registerUserWithEmailVerification(User user) {
        return emailVerificationService.send(user);
    }


    private VerificationStatus registerUserWithPhoneVerification(User user) {
        return phoneVerificationService.send(user);
    }

}