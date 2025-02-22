package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String addUser(@ModelAttribute("user") User user, Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        return "/admin/adduser";
    }

    @PostMapping("/admin/adduser")
    public String addUserPOST(@ModelAttribute("user") User user, BindingResult bindingResult,Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            bindingResult.rejectValue("username", "error.username",
                    "Username cannot be empty!");
            return "/admin/adduser";
        }

        if(user.getRoles().isEmpty()){
            bindingResult.rejectValue("roles", "error.userName",
                    "Should be at least 1 role!");
            return "/admin/adduser";
        }

        UserDetails existingUser = null;
        try {
            existingUser = userService.loadUserByUsername(user.getUsername());
        } catch (UsernameNotFoundException ignored) {
        }

        if (existingUser != null) {
            bindingResult.rejectValue("username", "error.username",
                    "This username is already taken!");
            return "/admin/adduser";
        }

        userService.addUser(user);
        return "redirect:/admin/allusers";
    }



    @PostMapping("/admin/delete")
    private String deleteUser(@RequestParam("id") long userId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authorisedUser = userService.loadUserByUsername(auth.getName());
        User userToDelete = userService.getUserById(userId);

        if (authorisedUser.getUsername().equals(userToDelete.getUsername())) {
            redirectAttributes.addFlashAttribute("deleteErrorUserId", userId);
            return "redirect:/admin/allusers";
        }

        userService.deleteUser(userToDelete);
        return "redirect:/admin/allusers";
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
    public String updateUser(@ModelAttribute("user") User user,BindingResult bindingResult,Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);

        List<User> allUsersList = userService.getAllUsers();
        allUsersList.remove(user);
        System.out.println(allUsersList);
        List<String> allUsernameWithoutCurrent = new ArrayList<>();

        for(User u : allUsersList){
            allUsernameWithoutCurrent.add(u.getUsername());
        }

        if(user.getRoles().isEmpty()){
            bindingResult.rejectValue("roles", "error.userName",
                    "Should be at least 1 role!");
            return "/admin/edituser";
        }

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        if (allUsernameWithoutCurrent.contains(user.getUsername())) {
            bindingResult.rejectValue("username", "error.userName",
                    "You cannot use this username!");
            return "/admin/edituser";
        }
        userService.updateUser(user.getId(), user.getUsername(), user.getEmail(),
                user.getAge(), user.getRoles(), user.getPassword());
        return "redirect:/admin/allusers";
    }
}
