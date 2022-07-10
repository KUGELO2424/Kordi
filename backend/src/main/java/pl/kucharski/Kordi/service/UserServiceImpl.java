package pl.kucharski.Kordi.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kucharski.Kordi.dto.UserDTO;
import pl.kucharski.Kordi.dto.UserRegistrationDTO;
import pl.kucharski.Kordi.entity.User;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.repository.UserRepository;
import pl.kucharski.Kordi.service.verification.VerificationService;
import pl.kucharski.Kordi.validator.EmailValidator;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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

    @Override
    @Transactional
    public String saveUser(UserRegistrationDTO user, boolean phoneVerification) {
        User foundUserByEmail = userRepository.findUserByEmail(user.getEmail()).orElse(null);
        User foundUserByUsername = userRepository.findUserByUsername(user.getUsername()).orElse(null);;
        User foundUserByPhone = userRepository.findUserByPhone(user.getPhone()).orElse(null);;

        // If given user attributes are the same, send verification token again
        if (foundUserByUsername != null && foundUserByEmail != null && foundUserByPhone != null) {
            if (foundUserByUsername.equals(foundUserByEmail) && foundUserByUsername.equals(foundUserByPhone)
                    && !foundUserByEmail.isEnabled()) {
                if (phoneVerification) {
                    return registerUserWithPhoneVerification(foundUserByEmail);
                } else {
                    return registerUserWithEmailVerification(foundUserByEmail);
                }
            }
        }

        if (!emailValidator.test(user.getEmail())) {
            throw new IllegalStateException("Email is not valid");
        } else if (foundUserByUsername != null) {
            throw new IllegalStateException("Username is already in use!");
        } else if (foundUserByEmail != null) {
            throw new IllegalStateException("Email is already in use!");
        } else if (foundUserByPhone != null) {
            throw new IllegalStateException("Phone number is already in use!");
        }

        User newUser = new User(user.getFirstName(), user.getLastName(), user.getUsername(),
                passwordEncoder.encode(user.getPassword()), user.getEmail(), user.getPhone(), false);
        userRepository.save(newUser);
        if (phoneVerification) {
            return registerUserWithPhoneVerification(newUser);
        } else {
            return registerUserWithEmailVerification(newUser);
        }
    }

    private String registerUserWithEmailVerification(User user) {
        return emailVerificationService.send(user);
    }

    private String registerUserWithPhoneVerification(User user) {
        return phoneVerificationService.send(user);
    }

    @Override
    public String verifyToken(UserDTO user, String token, boolean phoneVerification) {
        String response;
        if (phoneVerification) {
            response = phoneVerificationService.verify(user, token);
        } else {
            response = emailVerificationService.verify(user, token);
        }
       if (response.equals("verified") || response.equals("approved")) {
           enableUser(user);
       }
       return response;
    }

    public void enableUser(UserDTO user) {
        userRepository.enableUser(user.getUsername());
    }

    @Override
    public UserDTO getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found in database"));
        return new UserDTO(user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.isEnabled());
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found in database"));
        return new UserDTO(user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.isEnabled());
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found in database"));
        return new UserDTO(user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.isEnabled());
    }

    @Override
    public UserDTO getUserByPhone(String phone) {
        User user = userRepository.findUserByPhone(phone)
                .orElseThrow(() -> new UserNotFoundException("User not found in database"));
        return new UserDTO(user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.isEnabled());
    }

    @Override
    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream().map(user -> new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.isEnabled()
        )).collect(Collectors.toList());
    }

}