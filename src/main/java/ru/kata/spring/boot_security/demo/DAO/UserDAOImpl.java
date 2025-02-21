package ru.kata.spring.boot_security.demo.DAO;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;

import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addUser(User user) {
        entityManager.persist(user);
    }


    @Override
    public void deleteUser(User user) {
        entityManager.remove(entityManager.merge(user));
    }

    @Override
    public List<User> getAllUsers() {
        TypedQuery<User> query = entityManager.createQuery("from User", User.class);
        return query.getResultList();
    }

    @Override
    public User getUserById(long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void updateUser(long id,String name,String email,int age) {
        User user = entityManager.find(User.class,id);
        user.setUsername(name);
        user.setEmail(email);
        user.setAge(age);

    }

    @Override
    public User findUserByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);;
        query.setParameter("username",username);
        return query.getSingleResult();
    }

    @Override
    public List<Role> getAllRolesFromUser() {
        TypedQuery<Role> query = entityManager.createQuery("SELECT Role from User", Role.class);
        return query.getResultList();
    }
}
