package pl.kucharski.Kordi.service;

import org.springframework.http.ResponseEntity;
import pl.kucharski.Kordi.dto.UserRegistrationDto;
import pl.kucharski.Kordi.entity.User;

import java.util.List;

public interface UserService {
    String saveUser(UserRegistrationDto user, boolean phoneVerification);
    User getUserById(long id);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    User getUserByPhone(String phone);
    List<User> getUsers();
    String verifyToken(User user, String token, boolean phoneVerification);
}
