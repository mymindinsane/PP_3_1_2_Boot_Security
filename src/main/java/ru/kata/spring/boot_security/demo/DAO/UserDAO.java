package ru.kata.spring.boot_security.demo.DAO;


import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDAO  {

    void addUser(User user);
    void deleteUser(User user);
    List<User> getAllUsers();
    User getUserById(long id);
    void updateUser(long id, String name, String email, int age, List<Role> roles, String password);
    User findUserByUsername(String username);
    List<Role> getAllRolesFromUser();
    User findUserByEmail(String email);
}
