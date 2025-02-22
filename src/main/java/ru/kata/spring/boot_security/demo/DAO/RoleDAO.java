package ru.kata.spring.boot_security.demo.DAO;

import ru.kata.spring.boot_security.demo.Model.Role;

public interface RoleDAO {
    Role findRoleByRoleName(String roleName);
    void addRole(Role role);
}
