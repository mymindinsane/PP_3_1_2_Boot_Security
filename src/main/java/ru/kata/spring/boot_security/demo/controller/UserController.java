package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Service.UserService;


import java.util.List;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/allusers")
    public ModelAndView listUsers() {
        List<User> users = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @GetMapping("/adduser")
    public String addUser(@ModelAttribute("user") User user) {
        return "adduser";
    }

    @PostMapping("/adduser")
    public String addUserPOST(@ModelAttribute("user") User user) {
        userService.addUser(user);
        return "redirect:/allusers";
    }

    @PostMapping("/delete")
    private String deleteUser(@RequestParam("id") long userId) {
        userService.deleteUser(userService.getUserById(userId));
        return "redirect:/allusers";
    }

    @GetMapping("/edituser")
    public String editUser(@RequestParam("id") long userId,Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user",user);
        return "/edituser";
    }

    @PostMapping("/edituser")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.updateUser(user.getId(), user.getUsername(), user.getEmail(), user.getAge());
        return "redirect:/allusers";
    }
}
