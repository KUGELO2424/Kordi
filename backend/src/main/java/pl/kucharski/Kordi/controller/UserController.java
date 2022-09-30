package pl.kucharski.Kordi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.model.email.EmailToken;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.UserMapper;
import pl.kucharski.Kordi.model.user.UserRegistrationDTO;
import pl.kucharski.Kordi.service.user.UserService;
import pl.kucharski.Kordi.service.verification.EmailTokenService;


/**
 * User controller responsible for user management
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */
@RestController
public class UserController {

    private final UserService userService;
    private final EmailTokenService tokenService;
    private final UserMapper userMapper;

    public UserController(UserService userService, EmailTokenService tokenService, UserMapper userMapper) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    /**
     * Get all users created in system.
     *
     * @return list of users or empty list if no users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getUser() {
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Register new user in system, you can choose between EMAIL or PHONE verification.
     * After successful registration, token is sent to user email or phone, which is needed
     * to verify account by {@link UserController#verifyUser(String, String)}.
     *
     * @param verificationType type of verification, could be EMAIL or PHONE
     * @param user DTO of user to register
     * @return VerificationStatus - if token was sent correctly,<br>
     *         status 400 with message if UserRegisterException occurred
     */
    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestParam("verificationType") VerificationType verificationType,
                                      @RequestBody UserRegistrationDTO user) {
        VerificationStatus result;
        try {
            result = userService.saveUser(user, verificationType);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok(result);
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
        VerificationStatus result;
        try {
            if (phone != null) {
                UserDTO user = userService.getUserByPhone(phone);
                if (user == null) {
                    throw new IllegalStateException("User not found with given phone number");
                }
                result = userService.verifyToken(user, token, VerificationType.PHONE);
                return ResponseEntity.ok(result);
            }
            EmailToken emailToken = tokenService.getToken(token).orElseThrow(() -> {
                throw new IllegalStateException("User not found with given token");
            });
            User user = emailToken.getUser();
            UserDTO userDTO = userMapper.mapToUserDTO(user);
            result = userService.verifyToken(userDTO, token, VerificationType.EMAIL);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
