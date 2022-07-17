package pl.kucharski.Kordi.service.verification;

import pl.kucharski.Kordi.dto.UserDTO;
import pl.kucharski.Kordi.entity.User;
import pl.kucharski.Kordi.exception.UserRegisterException;
import pl.kucharski.Kordi.exception.UserVerifyException;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */
public interface VerificationService {

    /**
     * Send verification token to user
     *
     * @param user whom we sent verification token
     * @return token or information that token has been sent
     * @throws UserRegisterException with error message if it cannot register
     */
    String send(User user);

    /**
     * Verify user by given token
     *
     * @param user to verify
     * @return "verified" on email verification or "approved" on phone verification if success
     * @throws UserVerifyException with error message if it cannot verify
     */
    String verify(UserDTO user, String token);
}
