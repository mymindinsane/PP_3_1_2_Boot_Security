package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Service.UserService;

import java.security.Principal;

@Controller
public class HomeController {

    private final UserDAO userDAO;

    @Autowired
    public HomeController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:admin/";
    }

    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        User user = userDAO.findUserByUsername(authentication.getName());
        model.addAttribute("user",user);
        return "admin";
    }
}
