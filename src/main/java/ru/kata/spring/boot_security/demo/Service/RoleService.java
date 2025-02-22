package ru.kata.spring.boot_security.demo.Service;

import ru.kata.spring.boot_security.demo.Model.Role;

import java.util.List;

public interface RoleService {
    Role findRoleByRoleName(String roleName);
    void addRole(Role role);
    List<Role> getAllRoles();
}
