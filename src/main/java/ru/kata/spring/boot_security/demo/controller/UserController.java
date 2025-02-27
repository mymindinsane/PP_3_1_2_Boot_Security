package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Service.RoleService;
import ru.kata.spring.boot_security.demo.Service.UserService;


import java.util.*;

@RestController
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin/allusers")
    public ResponseEntity<List<User>> listUsers(Authentication authentication) {
        List<User> allusers = userService.getAllUsers();
        List<Role> roles = roleService.getAllRoles();
        User user = null;
        for (User u : allusers) {
            if (u.getUsername().equals(authentication.getName())) {
                user = u;
            }
        }
        return !allusers.isEmpty()
                ? new ResponseEntity<>(allusers, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user")
    public ResponseEntity<User> userInfo(Model model, Authentication authentication) {
        List<User> allusers = userService.getAllUsers();
        User user = null;
        for (User u : allusers) {
            if (u.getUsername().equals(authentication.getName())) {
                user = u;
            }
        }
        model.addAttribute("user", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/admin/adduser")
    public ResponseEntity<User> addUser(@ModelAttribute("user") User user, Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/admin/adduser")
    public ResponseEntity<?> addUserPOST(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (user.getRoles().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserDetails checkOnUsernameAlreadyTaken = null;
        try {
            checkOnUsernameAlreadyTaken = userService.loadUserByUsername(user.getUsername());
        } catch (UsernameNotFoundException ignored) {
        }

        if (checkOnUsernameAlreadyTaken != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        List<User> allUsersList = userService.getAllUsers();
        allUsersList.remove(user);
        List<String> allEmailsWithoutCurrent = new ArrayList<>();

        for (User u : allUsersList) {
            allEmailsWithoutCurrent.add(u.getEmail());
        }

        if (allEmailsWithoutCurrent.contains(user.getEmail())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }



        userService.addUser(user);
        return new ResponseEntity<>(user,HttpStatus.OK);

    }


    @DeleteMapping("/admin/delete")
    private ResponseEntity<?> deleteUser(@RequestParam("id") long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authorisedUser = userService.loadUserByUsername(auth.getName());
        User userToDelete = userService.getUserById(userId);

        if (authorisedUser.getUsername().equals(userToDelete.getUsername())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.deleteUser(userToDelete);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/admin/edituser")
    public ResponseEntity<?> editUser(@RequestParam("id") long userId, Model model) {
        User user = userService.getUserById(userId);
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PutMapping("/admin/edituser")
    public ResponseEntity<?> updateUser(@RequestBody User user) {

            System.out.println("Received user: " + user);
            // Логируем username, чтобы увидеть, что приходит
            System.out.println("Username: " + user.getUsername());

        List<User> allUsersList = userService.getAllUsers();
        allUsersList.remove(user);
        List<String> allUsernameWithoutCurrent = new ArrayList<>();

        for (User u : allUsersList) {
            allUsernameWithoutCurrent.add(u.getUsername());
        }

        List<String> allEmailsWithoutCurrent = new ArrayList<>();

        for (User u : allUsersList) {
            allEmailsWithoutCurrent.add(u.getEmail());
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (allUsernameWithoutCurrent.contains(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (allEmailsWithoutCurrent.contains(user.getEmail())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        User userBeforeUpdate = userService.getUserById(user.getId());
        String password = "";

        if (user.getPassword() == null) {
            password = userBeforeUpdate.getPassword();
        } else {
            password = user.getPassword();
        }

        userService.updateUser(user.getId(), user.getUsername(), user.getEmail(),
                user.getAge(), user.getRoles(), password);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getName().equals(user.getUsername())) {
            UserDetails updatedUserDetails = userService.loadUserByUsername(user.getUsername());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails,
                    auth.getCredentials(),
                    updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        if (user.getUsername().equals(auth.getName()) && !user.getRoles()
                .contains(roleService.findRoleByRoleName("ROLE_ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
