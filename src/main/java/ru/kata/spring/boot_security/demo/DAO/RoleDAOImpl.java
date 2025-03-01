package ru.kata.spring.boot_security.demo.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;


import java.util.List;

@Repository
public class RoleDAOImpl implements RoleDAO{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Role findRoleByRoleName(String roleName) {
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r WHERE r.roleName = :roleName",
                Role.class);
        query.setParameter("roleName",roleName);
        if (query.getResultList().isEmpty()){
            return null;
        } else {
            return query.getSingleResult();
        }
    }

    @Override
    public void addRole(Role role) {
        if (findRoleByRoleName(role.getRoleName()) == null){
            entityManager.persist(role);
        }
    }

    @Override
    public List<Role> getAllRoles() {
        TypedQuery<Role> query = entityManager.createQuery("from Role", Role.class);
        return query.getResultList();
    }
}
