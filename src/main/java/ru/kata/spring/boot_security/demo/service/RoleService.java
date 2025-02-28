package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

public interface RoleService {
    Role findRoleByRoleName(String roleName);
    void addRole(Role role);
    List<Role> getAllRoles();
}
