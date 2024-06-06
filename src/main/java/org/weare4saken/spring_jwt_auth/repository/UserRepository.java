package org.weare4saken.spring_jwt_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.weare4saken.spring_jwt_auth.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String userName);

    boolean existsByUsername(String userName);

    boolean existsByEmail(String email);
}