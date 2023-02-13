package pl.kucharski.Kordi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.service.user.UserService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_BAD_CREDENTIALS;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_VERIFIED_EMAIL;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_VERIFIED_PHONE;

/**
 * Filter responsible for authentication
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final Algorithm tokenAlgorithm;
    private final UserService userService;


    public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
                                      Algorithm tokenAlgorithm,
                                      UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenAlgorithm = tokenAlgorithm;
        this.userService = userService;
    }

    /**
     * Attempt authentication, if user cannot authenticate, write error to response
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException{
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Authentication attempt with credentials: {}", username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
            UserDTO user = userService.getUserByUsername(username);
            if (!user.isEnabled()) {
                log.warn("User {} not verified", username);
                if (user.getVerificationType().equals(VerificationType.EMAIL)) {
                    log.info("Sending email with verification link to user {} again", username);
                    userService.sendVerificationToken(user);
                    throw new DisabledException(USER_NOT_VERIFIED_EMAIL);
                } else {
                    throw new DisabledException(USER_NOT_VERIFIED_PHONE);
                }
            }
        } catch(Exception e) {
            log.warn("Cannot authenticate user");
            writeErrorToResponse(response, e);
        }
        return authentication;
    }

    /**
     * On successful authentication, create JWT and return it
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication)
            throws IOException {
        log.info("Authentication was successful. Generating JWT token...");
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
        User user = (User) authentication.getPrincipal();
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .withIssuer(request.getRequestURI())
                .withClaim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .sign(tokenAlgorithm);
        Map<String, String> token = new HashMap<>();
        token.put("access_token", accessToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        log.info("Returning generated token");
        new ObjectMapper().writeValue(response.getOutputStream(), token);
    }

    /**
     * On unsuccessful authentication, write error to response
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error", failed.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(401);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    private void writeErrorToResponse(HttpServletResponse response, Exception e) {
        Map<String, String> error = new HashMap<>();
        if (e.getMessage().equals("Bad credentials")) {
            error.put("error", USER_BAD_CREDENTIALS);
        } else {
            error.put("error", e.getMessage());
        }
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(401);
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
