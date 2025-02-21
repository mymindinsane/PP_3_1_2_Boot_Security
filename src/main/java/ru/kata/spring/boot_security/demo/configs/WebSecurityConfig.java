package ru.kata.spring.boot_security.demo.configs;

import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import ru.kata.spring.boot_security.demo.Service.UserServiceImpl;

@Configuration
@EnableWebSecurity

public class WebSecurityConfig {

    private final SuccessUserHandler successUserHandler;

    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index").permitAll()
                        .requestMatchers("/allusers","/adduser", "/edituser", "/delete").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .successHandler(successUserHandler)
                        .permitAll()

                )
                .logout(LogoutConfigurer::permitAll
                );

        return http.build();
    }

    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
*/


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserServiceImpl userServiceImpl) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        /*authenticationProvider.setPasswordEncoder(passwordEncoder());*/
        authenticationProvider.setUserDetailsService(userServiceImpl);
        return authenticationProvider;
    }
}