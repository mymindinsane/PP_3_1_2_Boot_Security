package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.Service.RoleService;
import ru.kata.spring.boot_security.demo.Service.UserService;


import java.net.Authenticator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
    public ModelAndView listUsers() {
        List<User> usersAndAdmins = userService.getAllUsers();
        List<User> onlyAdmins = userService.getUsersOnlyWithAdminRole(usersAndAdmins);
        List<User> allUsersExceptAdmins = userService.getAllUsersExceptAdmins(usersAndAdmins);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/admin/allusers");
        modelAndView.addObject("allUsersExceptAdmins", allUsersExceptAdmins);
        modelAndView.addObject("onlyAdmins", onlyAdmins);
        return modelAndView;
    }

    @GetMapping("/admin/adduser")
    public String addUser(@ModelAttribute("user") User user) {
        return "/admin/adduser";
    }

    @PostMapping("/admin/adduser")
    public String addUserPOST(@ModelAttribute("user") User user, BindingResult bindingResult) {
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        if (userDetails.getUsername().equals(user.getUsername())) {
            bindingResult.rejectValue("username", "error.userName",
                    "You cannot use this username!");
            return "/admin/adduser";
        }
        userService.addUser(user);
        return "redirect:/admin/allusers";
    }

    @PostMapping("/admin/delete")
    private String deleteUser(@RequestParam("id") long userId) {
        userService.deleteUser(userService.getUserById(userId));
        return "redirect:/admin/allusers";
    }

    @GetMapping("/admin/edituser")
    public String editUser(@RequestParam("id") long userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "/admin/edituser";
    }

    @PostMapping("/admin/edituser")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.updateUser(user.getId(), user.getUsername(), user.getEmail(), user.getAge());
        return "redirect:/admin/allusers";
    }
}
