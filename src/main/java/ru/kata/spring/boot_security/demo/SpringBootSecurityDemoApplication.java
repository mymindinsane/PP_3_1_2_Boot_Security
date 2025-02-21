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
		/*Role adminRole = new Role("ROLE_ADMIN");
		User user = new User("artik","{noop}123","banan@mail.ru",20,new ArrayList<>(List.of(adminRole)));
		UserService userService = context.getBean(UserService.class);
		userService.addUser(user);


		Role userRole = new Role("ROLE_USER");
		User user2 = new User("diana","{noop}123","banan@mail.ru",22,new ArrayList<>(List.of(userRole)));
		userService.addUser(user2);*/


		/*User user3 = new User("cat","{noop}123","banan@mail.ru",22,new ArrayList<>(List.of(userRole,adminRole)));
		userService.addUser(user3);*/

		//TODO:
		//remove the cascade creation of roles
		//add fields to html (checkboxes for roles)
		//implement bcrypt
		//logout button
	}

}
