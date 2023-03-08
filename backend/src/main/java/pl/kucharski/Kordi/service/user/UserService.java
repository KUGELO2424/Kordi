package pl.kucharski.Kordi.service.user;

import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.exception.UserAlreadyVerifiedException;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.UserRegistrationDTO;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.exception.UserRegisterException;
import pl.kucharski.Kordi.exception.InvalidPasswordException;
import pl.kucharski.Kordi.exception.UserVerifyException;

import java.util.List;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

public interface UserService {

    /**
     * Method for user register, user can register with EMAIL or PHONE verification
     *
     * @param user             data of user to register
     * @return VerificationStatus
     * @throws UserRegisterException with error message if it cannot register
     */
    VerificationStatus saveUser(UserRegistrationDTO user);

    /**
     * Send verification code to user. Can be used after registration
     *
     * @param user             data of user to send verification code
     * @return VerificationStatus
     * @throws UserAlreadyVerifiedException if user is already enabled
     */
    VerificationStatus sendVerificationToken(UserDTO user);

    /**
     * Verify user that has not verified yet.
     *
     * @param user             object of user to verify
     * @param token            send via email or sms for verification
     * @return VerificationStatus
     * @throws UserVerifyException with error message if it cannot verify user
     */
    VerificationStatus verifyToken(UserDTO user, String token);

    /**
     * Update user password if oldPassword match
     * @param username whom you want change password
     * @param oldPassword old password of user
     * @param newPassword new password for user
     *
     * @throws InvalidPasswordException if oldPassword does not match
     */
    void updatePassword(String username, String oldPassword, String newPassword);

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