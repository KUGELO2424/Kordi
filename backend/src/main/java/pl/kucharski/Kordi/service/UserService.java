package pl.kucharski.Kordi.service;

import pl.kucharski.Kordi.dto.UserDTO;
import pl.kucharski.Kordi.dto.UserRegistrationDTO;
import pl.kucharski.Kordi.entity.User;

import java.util.List;

public interface UserService {
    String saveUser(UserRegistrationDTO user, boolean phoneVerification);
    UserDTO getUserById(long id);
    UserDTO getUserByUsername(String username);
    UserDTO getUserByEmail(String email);
    UserDTO getUserByPhone(String phone);
    List<UserDTO> getUsers();
    String verifyToken(UserDTO user, String token, boolean phoneVerification);

}
