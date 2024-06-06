package org.weare4saken.spring_jwt_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.weare4saken.spring_jwt_auth.entity.Role;
import org.weare4saken.spring_jwt_auth.entity.RoleType;

import java.util.Collection;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findRolesByTypeIn(Collection<RoleType> roleTypes);
}
