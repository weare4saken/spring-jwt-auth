package org.weare4saken.spring_jwt_auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.weare4saken.spring_jwt_auth.entity.Role;
import org.weare4saken.spring_jwt_auth.entity.RoleType;
import org.weare4saken.spring_jwt_auth.entity.User;
import org.weare4saken.spring_jwt_auth.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return this.roleRepository.findAll();
    }

    public List<Role> getUserRoles(User user) {
        List<RoleType> userRoleTypes = user.getRoles().stream().map(Role::getType).toList();
        return this.roleRepository.findRolesByTypeIn(userRoleTypes);
    }
}
