package pl.kucharski.Kordi.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.exception.InvalidPasswordException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.email.EmailToken;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.UserMapper;
import pl.kucharski.Kordi.model.user.UserRegistrationDTO;
import pl.kucharski.Kordi.service.user.UserService;
import pl.kucharski.Kordi.service.verification.EmailTokenService;

import javax.validation.Valid;
import java.util.Collections;

import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_FOUND_WITH_GIVEN_TOKEN;


/**
 * User controller responsible for user management
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */
@Slf4j
@RestController
public class UserController {

    private final UserService userService;
    private final EmailTokenService tokenService;
    private final UserMapper userMapper;
    private final Algorithm tokenAlgorithm;

    public UserController(UserService userService, EmailTokenService tokenService, UserMapper userMapper, Algorithm tokenAlgorithm) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
        this.tokenAlgorithm = tokenAlgorithm;
    }

    /**
     * Get all users created in system.
     *
     * @return list of users or empty list if no users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        log.info("Request to get all users");
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Get user by username
     *
     * @param username username of user
     * @return list of users or empty list if no users
     */
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            log.info("Request to get user by username {}", username);
            UserDTO user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

    /**
     * Get logged user
     *
     * @return currently logged user
     */
    @GetMapping("/users/me")
    public ResponseEntity<?> getLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            log.info("Request to get logged user");
            UserDTO user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

    /**
     * Register new user in system, you can choose between EMAIL or PHONE verification.
     * After successful registration, token is sent to user email or phone, which is needed
     * to verify account by {@link UserController#verifyUser(String, String)}.
     *
     * @param user DTO of user to register
     * @return VerificationStatus - if token was sent correctly,<br>
     *         status 400 with message if UserRegisterException occurred
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveUser(@RequestBody @Valid UserRegistrationDTO user) {
        VerificationStatus result;
        try {
            log.info("Request to save user {}", user);
            result = userService.saveUser(user);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return ResponseEntity.ok(Collections.singletonMap("status", result));
    }

    /**
     * Send verification token again if user account was already created.
     * Then token is sent to user email or phone, which is needed to verify account
     * by {@link UserController#verifyUser(String, String)}.
     *
     * @param username username of user to send verification token
     * @return VerificationStatus - if token was sent correctly,<br>
     *         status 404 with message if UserNotFoundException occurred
     *         status 400 with message if some error occurred
     */
    @PostMapping(value = "/sendToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendVerificationTokenAgain(@RequestParam("username") String username) {
        VerificationStatus result;
        try {
            log.info("Request to send verification token for user {}", username);
            UserDTO userDTO = userService.getUserByUsername(username);
            result = userService.sendVerificationToken(userDTO);
        } catch (UserNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok(Collections.singletonMap("status", result));
    }

    /**
     * Verify existing user by a token. This endpoint is used in both type of verification EMAIL and PHONE.
     * If you want to verify token that was sent by PHONE verification, you need to specify additional
     * parameter which is user phone number
     *
     * @param token token sent during registration
     * @param phone user phone number if you want to verify token sent to your phone
     * @return VerificationStatus APPROVED if user verified,<br>
     *         400 with message if some problem occurred
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token,
                                        @RequestParam(value = "phone", required = false) String phone) {
        try {
            log.info("Request to verify user with token {} and phone number {}", token, phone);
            VerificationStatus result;
            if (phone != null) {
                result = verifyUserOnPhoneVerification(token, phone);
            } else {
                result = verifyUserOnEmailVerification(token);
            }
            return ResponseEntity.ok(Collections.singletonMap("status", result));
        } catch (UserNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    /**
     * Check if token with given username is valid
     *
     * @param username username of logged user
     * @param token JWT token of logged user
     * @return 200 if token is valid<br>
     *         400 if token not valid or param not given
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam(value = "username") String username, @RequestParam("token") String token) {
        String tokenWithoutBearer = token.substring(7);
        boolean validationResult = validateToken(username, tokenWithoutBearer);
        if (validationResult) {
            return ResponseEntity.ok(Collections.singletonMap("status", "Token valid"));
        }
        return ResponseEntity.badRequest().body("Token not valid");
    }

    /**
     * Update user password
     *
     * @param newPassword new password to set
     * @param oldPassword old password of user to check if correct
     * @return message if password changed<br>
     *         400 if new password invalid
     *         400 if old password does not match password
     */
    @PutMapping("/users/updatePassword")
    @CrossOrigin("${allowed.origins}")
    public ResponseEntity<?> updatedUserPassword(@RequestParam("password") String newPassword,
                                                 @RequestParam("oldPassword") String oldPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            log.info("Request to update password for user {}", username);
            userService.updatePassword(username, oldPassword, newPassword);
            return ResponseEntity.ok(Collections.singletonMap("status", "Password has been updated"));
        } catch (InvalidPasswordException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage()
            );
        }
    }

    private VerificationStatus verifyUserOnPhoneVerification(String token, String phone) {
        log.info("Verifying user with phone verification");
        UserDTO user = userService.getUserByPhone(phone);
        return userService.verifyToken(user, token);
    }

    private VerificationStatus verifyUserOnEmailVerification(String token) {
        log.info("Verifying user with email verification");
        EmailToken emailToken = tokenService.getToken(token).orElseThrow(() -> {
            throw new UserNotFoundException(USER_NOT_FOUND_WITH_GIVEN_TOKEN);
        });
        User user = emailToken.getUser();
        UserDTO userDTO = userMapper.mapToUserDTO(user);
        return userService.verifyToken(userDTO, token);
    }

    private boolean validateToken(String username, String token) {
        try {
            JWTVerifier verifier = JWT.require(tokenAlgorithm)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject().equals(username);
        } catch (JWTVerificationException exception){
            return false;
        }
    }

}
