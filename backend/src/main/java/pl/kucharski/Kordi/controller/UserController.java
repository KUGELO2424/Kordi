package pl.kucharski.Kordi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.UserRegistrationDTO;
import pl.kucharski.Kordi.model.email.EmailToken;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.service.user.UserService;
import pl.kucharski.Kordi.service.verification.EmailTokenService;


@RestController
public class UserController {

    private final UserService userService;
    private final EmailTokenService tokenService;

    public UserController(UserService userService, EmailTokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUser() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestParam("verificationType") VerificationType verificationType,
                                      @RequestBody UserRegistrationDTO user) {
        String result;
        try {
            result = userService.saveUser(user, verificationType);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token,
                                        @RequestParam(value = "phone", required = false) String phone) {
        String result;
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
            UserDTO userDTO = new UserDTO(user.getFirstName(), user.getLastName(),
                    user.getUsername(), user.getEmail(), user.getPhone(), user.isEnabled());
            result = userService.verifyToken(userDTO, token, VerificationType.EMAIL);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
