package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Service.RoleService;
import ru.kata.spring.boot_security.demo.Service.UserService;


import java.util.*;

@Controller
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin/allusers")
    public ModelAndView listUsers(Authentication authentication) {
        List<User> allusers = userService.getAllUsers();
        User user = null;
        for (User u : allusers){
            if (u.getUsername().equals(authentication.getName())){
                user = u;
            }
        }
        List<User> usersAndAdmins = userService.getAllUsers();
        List<User> onlyAdmins = userService.getUsersOnlyWithAdminRole(usersAndAdmins);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/admin/allusers");
        modelAndView.addObject("usersAndAdmins", usersAndAdmins);
        modelAndView.addObject("user",user);
        return modelAndView;
    }

    @GetMapping("/user")
    public String userInfo(Model model,Authentication authentication) {
        List<User> allusers = userService.getAllUsers();
        User user = null;
        for (User u : allusers){
            if (u.getUsername().equals(authentication.getName())){
                user = u;
            }
        }
        model.addAttribute("user", user);
        return "/user";
    }

    @GetMapping("/admin/adduser")
    public String addUser(@ModelAttribute("user") User user, Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        return "/admin/adduser";
    }

    @PostMapping("/admin/adduser")
    public String addUserPOST(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            bindingResult.rejectValue("username", "error.username",
                    "Username cannot be empty!");
            return "/admin/adduser";
        }

        if (user.getRoles().isEmpty()) {
            bindingResult.rejectValue("roles", "error.userName",
                    "Should be at least 1 role!");
            return "/admin/adduser";
        }

        UserDetails checkOnUsernameAlreadyTaken = null;
        try {
            checkOnUsernameAlreadyTaken = userService.loadUserByUsername(user.getUsername());
        } catch (UsernameNotFoundException ignored) {
        }

        if (checkOnUsernameAlreadyTaken != null) {
            bindingResult.rejectValue("username", "error.username",
                    "This username is already taken!");
            return "/admin/adduser";
        }

        List<User> allUsersList = userService.getAllUsers();
        allUsersList.remove(user);
        List<String> allEmailsWithoutCurrent = new ArrayList<>();

        for (User u : allUsersList) {
            allEmailsWithoutCurrent.add(u.getEmail());
        }

        if (allEmailsWithoutCurrent.contains(user.getEmail())) {
            bindingResult.rejectValue("email", "error.email",
                    "This email is already taken!");
            return "/admin/edituser";
        }

        userService.addUser(user);
        return "redirect:/admin";
    }


    @PostMapping("/admin/delete")
    private String deleteUser(@RequestParam("id") long userId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authorisedUser = userService.loadUserByUsername(auth.getName());
        User userToDelete = userService.getUserById(userId);

        if (authorisedUser.getUsername().equals(userToDelete.getUsername())) {
            redirectAttributes.addFlashAttribute("deleteErrorUserId", userId);
            return "redirect:/admin";
        }

        userService.deleteUser(userToDelete);
        return "redirect:/admin";
    }


    @GetMapping("/admin/edituser")
    public String editUser(@RequestParam("id") long userId, Model model) {
        User user = userService.getUserById(userId);
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "/admin/edituser";
    }

    @PostMapping("/admin/edituser")
    public String updateUser(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);

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
            bindingResult.rejectValue("roles", "error.userName",
                    "Should be at least 1 role!");
            return "/admin/edituser";
        }

        if (allUsernameWithoutCurrent.contains(user.getUsername())) {
            bindingResult.rejectValue("username", "error.userName",
                    "This username is already taken!");
            return "/admin/edituser";
        }

        if (allEmailsWithoutCurrent.contains(user.getEmail())) {
            bindingResult.rejectValue("email", "error.email",
                    "This email is already taken!");
            return "/admin/edituser";
        }
        User userBeforeUpdate = userService.getUserById(user.getId());
        String password = "";

        if(user.getPassword() == null){
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
                .contains(roleService.findRoleByRoleName("ROLE_ADMIN"))){
            return "redirect:/user";
        }
        return "redirect:/admin";
    }


}
