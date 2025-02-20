package ru.kata.spring.boot_security.demo.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    @Override
    public void addUser(User user) {
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
    public void updateUser(long id,String name,String email,int age) {
         userDAO.updateUser(id,name,email,age);
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
            if (hasAdminRole){
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
            if (!hasAdminRole){
                allUsersExceptAdmins.add(user);
            }
        }
        return allUsersExceptAdmins;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findUserByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),
                User.mapRolesToAuthorities(user.getRoles()));
    }



}
