package pl.kucharski.Kordi.service;

import pl.kucharski.Kordi.entity.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User getUserById(long id);
    User getUserByUsername(String username);
    List<User> getUsers();
}
