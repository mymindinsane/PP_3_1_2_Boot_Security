package ru.kata.spring.boot_security.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.ArrayList;
import java.util.List;


@EnableJpaRepositories()
@SpringBootApplication
public class SpringBootSecurityDemoApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
		RoleService userServiceRole = context.getBean(RoleService.class);
		userServiceRole.addRole(new Role("ROLE_ADMIN"));
		userServiceRole.addRole(new Role("ROLE_USER"));
		UserService userServiceUser = context.getBean(UserService.class);
		userServiceUser.addUser(new User("useradmin",
				"123",
				"useradmin@mail.ru", 20,new ArrayList<>
				(List.of(userServiceRole.findRoleByRoleName("ROLE_ADMIN"),
						userServiceRole.findRoleByRoleName("ROLE_USER")))));



		userServiceUser.addUser(new User("user",
				"456","user@mail.ru",22,new ArrayList<>
				(List.of(userServiceRole.findRoleByRoleName("ROLE_USER")))));


		userServiceUser.addUser(new User("admin",
				"789","admin@mail.ru",25,new ArrayList<>
				(List.of(userServiceRole.findRoleByRoleName("ROLE_ADMIN")))));

	}

}
