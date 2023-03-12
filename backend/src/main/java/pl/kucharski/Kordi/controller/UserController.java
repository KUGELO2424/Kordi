package pl.kucharski.Kordi.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.util.List;

import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_FOUND_WITH_GIVEN_TOKEN;


/**
 * User controller responsible for user management
 *
 * @author Grzegorz Kucharski gelo2424@wp.pl
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

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of users"),
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        log.info("Request to get all users");
        return ResponseEntity.ok(userService.getUsers());
    }

    @Operation(summary = "Get user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user"),
            @ApiResponse(responseCode = "404", description = "username not found", content = @Content),
    })
    @GetMapping("/users/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@Parameter(description = "username of user") @PathVariable String username) {
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

    @Operation(summary = "Get logged user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "currently logged user"),
            @ApiResponse(responseCode = "404", description = "username from token not found", content = @Content),
    })
    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> getLoggedUser() {
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

    @Operation(summary = "Register new user", description = "Register new user in system, you can choose between EMAIL or PHONE verification. " +
            "After successful registration, token is sent to user email or phone, which is needed " +
            "to verify account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "verification status",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VerificationStatus.class)) }),
            @ApiResponse(responseCode = "400", description = "cannot register user", content = @Content),
    })
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "user to register") @RequestBody @Valid UserRegistrationDTO user) {
        VerificationStatus result;
        try {
            log.info("Request to save user {}", user);
            result = userService.saveUser(user);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return ResponseEntity.ok(Collections.singletonMap("status", result));
    }

    @Operation(summary = "Send verification token", description = "Send verification token again if user account was already created. " +
            "Then token is sent to user email or phone, which is needed to verify account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "verification status",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VerificationStatus.class)) }),
            @ApiResponse(responseCode = "400", description = "cannot send token again", content = @Content),
            @ApiResponse(responseCode = "404", description = "username not found", content = @Content),
    })
    @PostMapping(value = "/sendToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendVerificationTokenAgain(@Parameter(description = "username of user to send verification token") @RequestParam("username") String username) {
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

    @Operation(summary = "Verify user", description = "Verify existing user by a token. This endpoint is used in both " +
            "type of verification EMAIL and PHONE. If you want to verify token that was sent by PHONE verification, " +
            "you need to specify additional parameter which is user phone number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "verification status",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VerificationStatus.class)) }),
            @ApiResponse(responseCode = "400", description = "cannot send token again", content = @Content),
            @ApiResponse(responseCode = "404", description = "user not found", content = @Content),
    })
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@Parameter(description = "token sent during registration") @RequestParam("token") String token,
                                        @Parameter(description = "user phone number if you want to verify token sent to your phone") @RequestParam(value = "phone", required = false) String phone) {
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

    @Operation(summary = "Validate jwt token", description = "Check if token with given username is valid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "token is valid"),
            @ApiResponse(responseCode = "400", description = "token not valid or param not given", content = @Content),
    })
    @PostMapping("/validate")
    public ResponseEntity<?> validate(@Parameter(description = "username of logged user") @RequestParam(value = "username") String username,
                                      @Parameter(description = "JWT token of logged user") @RequestParam("token") String token) {
        String tokenWithoutBearer = token.substring(7);
        boolean validationResult = validateToken(username, tokenWithoutBearer);
        if (validationResult) {
            return ResponseEntity.ok(Collections.singletonMap("status", "Token valid"));
        }
        return ResponseEntity.badRequest().body("Token not valid");
    }

    @Operation(summary = "Update user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "password changed"),
            @ApiResponse(responseCode = "400", description = "new password invalid", content = @Content),
            @ApiResponse(responseCode = "400", description = "old password does not match password", content = @Content),
    })
    @PutMapping("/users/updatePassword")
    @CrossOrigin("${allowed.origins}")
    public ResponseEntity<?> updatedUserPassword(@Parameter(description = "new password to set") @RequestParam("password") String newPassword,
                                                 @Parameter(description = "old password of user") @RequestParam("oldPassword") String oldPassword) {
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
