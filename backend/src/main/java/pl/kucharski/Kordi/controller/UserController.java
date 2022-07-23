package pl.kucharski.Kordi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kucharski.Kordi.dto.UserDTO;
import pl.kucharski.Kordi.dto.UserRegistrationDTO;
import pl.kucharski.Kordi.entity.EmailToken;
import pl.kucharski.Kordi.entity.User;
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
    public ResponseEntity<?> saveUser(@RequestParam("phoneVerification") boolean phoneVerification,
                                      @RequestBody UserRegistrationDTO user) {
        String result = "";
        try {
            result = userService.saveUser(user, phoneVerification);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token,
                                        @RequestParam(value = "phone", required = false) String phone) {
        String result = "";
        try {
            if (phone != null) {
                UserDTO user = userService.getUserByPhone(phone);
                if (user == null) {
                    throw new IllegalStateException("User not found with given phone number");
                }
                result = userService.verifyToken(user, token, true);
                return ResponseEntity.ok(result);
            }
            EmailToken emailToken = tokenService.getToken(token).orElseThrow(() -> {
                throw new IllegalStateException("User not found with given token");
            });
            User user = emailToken.getUser();
            UserDTO userDTO = new UserDTO(user.getFirstName(), user.getLastName(),
                    user.getUsername(), user.getEmail(), user.getPhone(), user.isEnabled());
            result = userService.verifyToken(userDTO, token, false);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
