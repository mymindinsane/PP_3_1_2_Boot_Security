package ru.kata.spring.boot_security.demo.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.DAO.RoleDAO;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;


import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, RoleDAO roleDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void addUser(User user) {
        List<Role> userRoles = user.getRoles();
        List<Role> persistentRoles = new ArrayList<>();

        for (Role role : userRoles) {
            Role existingRole = roleDAO.findRoleByRoleName(role.getRoleName());
            if (existingRole == null) {
                existingRole = new Role(role.getRoleName());
                roleDAO.addRole(existingRole);
            }
            persistentRoles.add(existingRole);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(persistentRoles);
        userDAO.addUser(user);
    }


    @Transactional
    @Override
    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Transactional
    @Override
    public User getUserById(long id) {
        return userDAO.getUserById(id);
    }

    @Transactional
    @Override
    public void updateUser(long id, String name, String email, int age, List<Role> roles, String password) {
        List<Role> persistentRoles = new ArrayList<>();

        for (Role role : roles) {
            Role existingRole = roleDAO.findRoleByRoleName(role.getRoleName());
            if (existingRole == null) {
                existingRole = new Role(role.getRoleName());
                roleDAO.addRole(existingRole);
            }
            persistentRoles.add(existingRole);
        }
        User userBeforeUpdate = getUserById(id);
        roles = persistentRoles;
        if (password.equals(userBeforeUpdate.getPassword())) {
            userDAO.updateUser(id, name, email, age, roles, password);
        } else {
            userDAO.updateUser(id, name, email, age, roles, passwordEncoder.encode(password));
        }
    }


    @Override
    public List<User> getUsersOnlyWithAdminRole(List<User> allUsers) {
        List<User> onlyAdmins = new ArrayList<>();
        for (User user : allUsers) {
            List<Role> roles = user.getRoles();
            boolean hasAdminRole = false;
            for (Role role : roles) {
                if (!roles.isEmpty() && role.getRoleName().equals("ROLE_ADMIN")) {
                    hasAdminRole = true;
                    break;
                }
            }
            if (hasAdminRole) {
                onlyAdmins.add(user);
            }
        }
        return onlyAdmins;
    }

    @Override
    public List<User> getAllUsersExceptAdmins(List<User> allUsers) {
        List<User> allUsersExceptAdmins = new ArrayList<>();
        for (User user : allUsers) {
            List<Role> roles = user.getRoles();
            boolean hasAdminRole = false;
            for (Role role : roles) {
                if (!roles.isEmpty() && role.getRoleName().equals("ROLE_ADMIN")) {
                    hasAdminRole = true;
                    break;
                }
            }
            if (!hasAdminRole) {
                allUsersExceptAdmins.add(user);
            }
        }
        return allUsersExceptAdmins;
    }

    @Override
    public User findUserByEmail(String email) {
        return userDAO.findUserByEmail(email);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " not found");
        }
        return user;
    }
}


