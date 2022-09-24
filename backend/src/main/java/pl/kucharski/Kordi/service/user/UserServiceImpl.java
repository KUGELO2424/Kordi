package pl.kucharski.Kordi.service.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.UserMapper;
import pl.kucharski.Kordi.model.user.UserRegistrationDTO;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.exception.UserRegisterException;
import pl.kucharski.Kordi.repository.UserRepository;
import pl.kucharski.Kordi.service.verification.VerificationService;
import pl.kucharski.Kordi.validator.EmailValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;
    private final VerificationService emailVerificationService;
    private final VerificationService phoneVerificationService;

    public UserServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder,
                           EmailValidator emailValidator,
                           @Qualifier("emailVerificationService") VerificationService emailVerificationService,
                           @Qualifier("phoneVerificationService") VerificationService phoneVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = emailValidator;
        this.emailVerificationService = emailVerificationService;
        this.phoneVerificationService = phoneVerificationService;
    }

    /**
     * @see UserDetailsService#loadUserByUsername(String)
     * @throws DisabledException when user found but is not enabled
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found in database"));
        if (!user.isEnabled()) {
            throw new DisabledException("User is not verified");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    /**
     * @see UserService#saveUser(UserRegistrationDTO, VerificationType)
     */
    @Override
    @Transactional
    public String saveUser(UserRegistrationDTO user, VerificationType verificationType) {
        User foundUserByEmail = userRepository.findUserByEmail(user.getEmail()).orElse(null);
        User foundUserByUsername = userRepository.findUserByUsername(user.getUsername()).orElse(null);
        User foundUserByPhone = userRepository.findUserByPhone(user.getPhone()).orElse(null);

        // If given user attributes are the same, send verification token again
        if (foundUserByUsername != null && foundUserByEmail != null && foundUserByPhone != null) {
            if (foundUserByUsername.equals(foundUserByEmail) && foundUserByUsername.equals(foundUserByPhone)
                    && !foundUserByEmail.isEnabled()) {
                if (verificationType == VerificationType.PHONE) {
                    return registerUserWithPhoneVerification(foundUserByEmail);
                } else {
                    return registerUserWithEmailVerification(foundUserByEmail);
                }
            }
        }

        if (!emailValidator.test(user.getEmail())) {
            throw new UserRegisterException("Email is not valid");
        } else if (foundUserByUsername != null) {
            throw new UserRegisterException("Username is already in use!");
        } else if (foundUserByEmail != null) {
            throw new UserRegisterException("Email is already in use!");
        } else if (foundUserByPhone != null) {
            throw new UserRegisterException("Phone number is already in use!");
        }

        User newUser = new User(user.getFirstName(), user.getLastName(), user.getUsername(),
                passwordEncoder.encode(user.getPassword()), user.getEmail(), user.getPhone(), false);

        userRepository.save(newUser);
        if (verificationType == VerificationType.PHONE) {
            return registerUserWithPhoneVerification(newUser);
        } else {
            return registerUserWithEmailVerification(newUser);
        }
    }



    /**
     * @see UserService#verifyToken(UserDTO, String, VerificationType)
     */
    @Override
    @Transactional
    public String verifyToken(UserDTO user, String token, VerificationType verificationType) {
        String response;
        if (verificationType == VerificationType.PHONE) {
            response = phoneVerificationService.verify(user, token);
        } else {
            response = emailVerificationService.verify(user, token);
        }
       if (response.equals("verified") || response.equals("approved")) {
           enableUser(user);
       }
       return response;
    }


    /**
     * @see UserService#getUserById(long)
     */
    @Override
    public UserDTO getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found in database"));
        return UserMapper.mapUserDTOFromUser(user);
    }

    /**
     * @see UserService#getUserByUsername(String) (long)
     */
    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found in database"));
        return UserMapper.mapUserDTOFromUser(user);
    }

    /**
     * @see UserService#getUserByEmail(String)
     */
    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found in database"));
        return UserMapper.mapUserDTOFromUser(user);
    }

    /**
     * @see UserService#getUserByPhone(String)
     */
    @Override
    public UserDTO getUserByPhone(String phone) {
        User user = userRepository.findUserByPhone(phone)
                .orElseThrow(() -> new UserNotFoundException("User not found in database"));
        return UserMapper.mapUserDTOFromUser(user);
    }

    /**
     * @see UserService#getUsers()
     */
    @Override
    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream().map(UserMapper::mapUserDTOFromUser).collect(Collectors.toList());
    }

    private void enableUser(UserDTO user) {
        userRepository.enableUser(user.getUsername());
    }


    private String registerUserWithEmailVerification(User user) {
        return emailVerificationService.send(user);
    }


    private String registerUserWithPhoneVerification(User user) {
        return phoneVerificationService.send(user);
    }

}