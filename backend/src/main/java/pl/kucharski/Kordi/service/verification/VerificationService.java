package pl.kucharski.Kordi.service.verification;

import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.User;
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
     * @return VerificationStatus
     * @throws UserRegisterException with error message if it cannot register
     */
    VerificationStatus send(User user);

    /**
     * Verify user by given token
     *
     * @param user user to verify
     * @return VerificationStatus
     * @throws UserVerifyException with error message if it cannot verify
     */
    VerificationStatus verify(UserDTO user, String token);
}
