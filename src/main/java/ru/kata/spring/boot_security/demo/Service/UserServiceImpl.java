package ru.kata.spring.boot_security.demo.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.Model.User;



import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    @Override
    public void addUser(User user) {
        userDAO.addUser(user);
    }

    @Transactional
    @Override
    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Transactional
    @Override
    public User getUserById(long id) {
        return userDAO.getUserById(id);
    }

    @Transactional
    @Override
    public void updateUser(long id,String name,String email,int age) {
         userDAO.updateUser(id,name,email,age);
    }
}
