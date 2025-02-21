package ru.kata.spring.boot_security.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.DAO.UserDAOImpl;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@EnableJpaRepositories()
@SpringBootApplication
public class SpringBootSecurityDemoApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
		Role adminRole = new Role("ROLE_ADMIN");
		User user = new User("artik","{noop}123","banan@mail.ru",20,new ArrayList<>(List.of(adminRole)));
		UserService userService = context.getBean(UserService.class);
		userService.addUser(user);

		//TODO:
		//prevent you from deleting the user you are logged in as. (maybe principal)
		//remove the cascade creation of roles
		//add fields to html (checkboxes for roles)
		//thymeleaf spring security add dependency and make different views for different roles
		//implement bcrypt
		//logout button
	}

}
