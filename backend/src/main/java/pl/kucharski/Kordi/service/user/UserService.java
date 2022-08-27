package pl.kucharski.Kordi.service.user;

import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.UserRegistrationDTO;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.exception.UserRegisterException;
import pl.kucharski.Kordi.exception.UserVerifyException;

import java.util.List;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

public interface UserService {

    /**
     * Method for user register, it requires email or phone verification
     *
     * @param user              data of user to register
     * @param phoneVerification true to enable phone verification, false to use email verification
     * @return emailToken on email verification or "pending" on phone verification
     * @throws UserRegisterException with error message if it cannot register
     */
    String saveUser(UserRegistrationDTO user, boolean phoneVerification);

    /**
     * Verify user that has not verified yet.
     *
     * @param user              object of user to verify
     * @param token             send via email or sms for verification
     * @param phoneVerification true to verify code send via sms, false to verify by email token
     * @return "verified" on email verification or "approved" on phone verification if success
     * @throws UserVerifyException with error message if it cannot verify user
     */
    String verifyToken(UserDTO user, String token, boolean phoneVerification);

    /**
     * Find user with given id.
     *
     * @param id of user
     * @return object of UserDTO if user found
     * @throws UserNotFoundException if no user in database
     */
    UserDTO getUserById(long id);

    /**
     * Find user with given username
     *
     * @param username of user
     * @return object of UserDTO if user found
     * @throws UserNotFoundException if no user in database
     */
    UserDTO getUserByUsername(String username);

    /**
     * Find user with given username
     *
     * @param email of user
     * @return object of UserDTO if user found
     * @throws UserNotFoundException if no user in database
     */
    UserDTO getUserByEmail(String email);

    /**
     * Find user with given username
     *
     * @param phone of user
     * @return object of UserDTO if user found
     * @throws UserNotFoundException if no user in database
     */
    UserDTO getUserByPhone(String phone);

    /**
     * @return list of users
     */
    List<UserDTO> getUsers();
}