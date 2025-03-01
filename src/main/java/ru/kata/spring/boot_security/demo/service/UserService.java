package ru.kata.spring.boot_security.demo.service;



import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {
     void addUser(User user);
     void deleteUser(User user);
     List<User> getAllUsers();
     User getUserById(long id);
     void updateUser(long id, String name, String email, int age, List<Role> roles, String password);
     List<User> getUsersOnlyWithAdminRole(List<User> allUsers);
     List<User> getAllUsersExceptAdmins(List<User> allUsers);
     User findUserByEmail(String email);


}
