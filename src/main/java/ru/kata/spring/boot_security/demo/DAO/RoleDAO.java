package ru.kata.spring.boot_security.demo.DAO;

import ru.kata.spring.boot_security.demo.Model.Role;

import java.util.List;

public interface RoleDAO {
    Role findRoleByRoleName(String roleName);
    void addRole(Role role);
    List<Role> getAllRoles();
}
